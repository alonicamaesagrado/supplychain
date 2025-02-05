package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.SalesorderDetailDto;
import com.informatics.supplychain.dto.SalesorderDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.model.Salesorder;
import com.informatics.supplychain.model.SalesorderDetail;
import com.informatics.supplychain.service.CustomerService;
import com.informatics.supplychain.service.InventoryService;
import com.informatics.supplychain.service.ItemService;
import com.informatics.supplychain.service.SalesorderService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SalesorderController {

    @Autowired
    private SalesorderService salesorderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    CustomerService customerService;

    @GetMapping("v1/salesorder")
    ResponseEntity<?> getSalesorder(@RequestParam String salesorderNo) {
        var salesorder = salesorderService.findBySalesorderNo(salesorderNo);

        if (salesorder == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        return ResponseEntity.ok(new SalesorderDto(salesorder));
    }

    @GetMapping("v1/salesorderList")
    ResponseEntity<List<SalesorderDto>> getSalesorderList(@RequestParam(required = false) TransactionStatusEnum status) {
        List<Salesorder> salesorder;

        if (status != null) {
            salesorder = salesorderService.findByStatus(status);
        } else {
            salesorder = salesorderService.findAll();
        }
        List<SalesorderDto> salesorderDtos = salesorder.stream().map(SalesorderDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(salesorderDtos);
    }

    @GetMapping("v1/salesorderList/{itemId}")
    public ResponseEntity<?> getSalesorderListByItemId(@PathVariable Integer itemId) {
        List<Salesorder> salesorders = salesorderService.findByItemId(itemId);
        if (salesorders.isEmpty()) {
            return ResponseEntity.status(404).body("No transactions found.");
        }
        List<SalesorderDto> salesorderDtos = salesorders.stream().map(SalesorderDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(salesorderDtos);
    }

    @PostMapping("v1/salesorder")
    public ResponseEntity<?> saveSalesorder(@RequestBody SalesorderDto salesorderDto) throws Exception {
        if (salesorderDto.getOrderDate() == null) {
            return ResponseEntity.status(400).body("Order date cannot be null.");
        }

        //creation of salesorder
        var salesorder = new Salesorder(salesorderDto);
        var customer = customerService.findByIdAndStatus(salesorderDto.getCustomer().getId(), StatusEnum.ACTIVE);
        String yearMonth = salesorderDto.getOrderDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = salesorderService.getNextSeriesNumber(yearMonth);
        String salesorderNo = "SO" + yearMonth + String.format("%04d", series);
        salesorder.setCustomer(customer);
        salesorder.setSalesorderNo(salesorderNo);
        salesorder.setStatus(TransactionStatusEnum.DRAFT);
        salesorder.setCreatedDateTime(LocalDateTime.now());

        salesorder.setDetails(new ArrayList<>());

        //validations and creation on inventory
        for (SalesorderDetailDto detailDto : salesorderDto.getDetails()) {
            var item = itemService.findByCode(detailDto.getItem().getCode());
            if (item == null) {
                return ResponseEntity.status(404).body("Item does not exist: " + detailDto.getItem().getCode());
            }

            var inventory = inventoryService.findByItemIdAndItemType(item.getId(), item.getCategory());
            if (inventory == null) {
                inventory = new Inventory();
                inventory.setItem(item);
                inventory.setItemType(item.getCategory());
                inventory.setInQuantity(0.0);
            }
            if (detailDto.getOrderQuantity() > (inventory.getInQuantity() - inventory.getOutQuantity())) {
                return ResponseEntity.status(404).body("Cannot deliver more than available quantity.");
            }

            //creation of salesorder_detail
            var detail = new SalesorderDetail();
            double itemPrice = (detailDto.getItemPrice() != null) ? detailDto.getItemPrice() : item.getPrice();

            detail.setSalesorder(salesorder);
            detail.setItem(item);
            detail.setOrderQuantity(detailDto.getOrderQuantity());
            detail.setItemPrice(itemPrice);
            detail.setAmount(detailDto.getOrderQuantity() * itemPrice);
            salesorder.getDetails().add(detail);
        }
        salesorder = salesorderService.save(salesorder);

        // Updating inventory
        for (SalesorderDetail detail : salesorder.getDetails()) {
            var inventory = inventoryService.findByItemIdAndItemType(detail.getItem().getId(), detail.getItem().getCategory());
            inventory.setOutQuantity(inventory.getOutQuantity() + detail.getOrderQuantity());
            inventoryService.save(inventory);
        }
        return ResponseEntity.ok(new SalesorderDto(salesorder));
    }

    @PutMapping("v1/salesorder/{salesorderNo}")
    public ResponseEntity<?> updateSalesOrder(@PathVariable String salesorderNo, @RequestBody SalesorderDto salesorderDto) throws Exception {
        var salesorder = salesorderService.findBySalesorderNo(salesorderNo);
        if (salesorder == null) {
            return ResponseEntity.status(404).body("Sales order not found.");
        }

        //salesorder summary details to be update
        if (salesorderDto.getOrderDate() != null) {
            salesorder.setOrderDate(salesorderDto.getOrderDate()); //date
        }
        if (salesorderDto.getRemarks() != null) {
            salesorder.setRemarks(salesorderDto.getRemarks()); //remarks
        }
        if (salesorderDto.getCustomer() != null) {
            var customer = customerService.findByIdAndStatus(salesorderDto.getCustomer().getId(), StatusEnum.ACTIVE);
            if (customer == null) {
                return ResponseEntity.status(404).body("Customer not found or inactive.");
            }
            salesorder.setCustomer(customer); //customer
        }
        if (salesorderDto.getStatus() != null) {
            salesorder.setStatus(salesorderDto.getStatus()); //status
        }

        //salesorder details to be update
        for (SalesorderDetailDto detailDto : salesorderDto.getDetails()) {
            var detail = salesorder.getDetails().stream().filter(d -> d.getId().equals(detailDto.getId())).findFirst().orElse(null);
            var item = itemService.findByCode(detailDto.getItem().getCode());
            var inventory = inventoryService.findByItemIdAndItemType(item.getId(), item.getCategory());
            double previousOrderQuantity = detail.getOrderQuantity();
            double quantityDifference = detailDto.getOrderQuantity() - previousOrderQuantity;
            
            //validations
            if (detail == null) {
                return ResponseEntity.status(404).body("Sales order detail not found.");
            }
            if (item == null) {
                return ResponseEntity.status(404).body("Item does not exist: " + detailDto.getItem().getCode());
            }
            if (inventory == null) {
                return ResponseEntity.status(404).body("Inventory not found for item: " + item.getCode());
            }
            if ((inventory.getInQuantity() - inventory.getOutQuantity()) < quantityDifference) {
                return ResponseEntity.status(404).body("Cannot deliver more than available quantity.");
            }

            //salesorder details
            detail.setOrderQuantity(detailDto.getOrderQuantity()); // order quantity
            if (detailDto.getItemPrice() != null) {
                detail.setItemPrice(detailDto.getItemPrice()); // item price
            }
            detail.setAmount(detail.getOrderQuantity() * detail.getItemPrice()); //amount
            inventory.setOutQuantity(inventory.getOutQuantity() + quantityDifference); //update out quantity in inventory
            inventoryService.save(inventory);
        }
        salesorder = salesorderService.save(salesorder);
        return ResponseEntity.ok(new SalesorderDto(salesorder));
    }
}
