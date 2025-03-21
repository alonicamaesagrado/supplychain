package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.SalesorderDetailDto;
import com.informatics.supplychain.dto.SalesorderDetailForArimaDto;
import com.informatics.supplychain.dto.SalesorderDto;
import com.informatics.supplychain.dto.SalesorderForArimaDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.model.Salesorder;
import com.informatics.supplychain.model.SalesorderDetail;
import com.informatics.supplychain.service.CustomerService;
import com.informatics.supplychain.service.InventoryService;
import com.informatics.supplychain.service.ItemService;
import com.informatics.supplychain.service.SalesorderService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SalesorderController extends BaseController {

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
    ResponseEntity<List<SalesorderDto>> getSalesorderList(@RequestParam(required = false) TransactionStatusEnum status,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<Salesorder> salesorder;

        if (fromDate != null && toDate != null) {
            salesorder = (status != null) ? salesorderService.findByStatusAndOrderDateBetween(status, fromDate, toDate) : salesorderService.findByOrderDateBetween(fromDate, toDate);
        } else if (customerId != null) {
            salesorder = salesorderService.findByCustomerId(customerId);
        } else if (customerId != null && fromDate != null && toDate != null) {
            salesorder = (status != null) ? salesorderService.findByCustomerIdAndStatusAndOrderDateBetween(customerId, status, fromDate, toDate) : salesorderService.findByCustomerIdAndOrderDateBetween(customerId, fromDate, toDate);
        } else {
            salesorder = (status != null) ? salesorderService.findByStatus(status) : salesorderService.findAll();
        }
        List<SalesorderDto> salesorderDtos = salesorder.stream().map(SalesorderDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(salesorderDtos);
    }

    @GetMapping("v1/salesorderForArima")
    ResponseEntity<List<SalesorderForArimaDto>> getSalesorderForArima(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<Salesorder> salesorder;

        if (fromDate != null && toDate != null) {
            salesorder = salesorderService.findByOrderDateBetween(fromDate, toDate);
        } else {
            salesorder = salesorderService.findAll();
        }
        List<SalesorderForArimaDto> salesorderForArimaDtos = salesorder.stream().map(e -> new SalesorderForArimaDto(e,
                e.getDetails().stream().map(x -> new SalesorderDetailForArimaDto(x)).collect(Collectors.toList()))).collect(Collectors.toList());
        return ResponseEntity.ok(salesorderForArimaDtos);
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
    public ResponseEntity<?> saveSalesorder(@RequestHeader String usercode, @RequestHeader String token, @RequestBody SalesorderDto salesorderDto) throws Exception {
        if (!verify(usercode, token)) {
            return ResponseEntity.badRequest().build();
        }
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
        salesorder.setCreatedBy(usercode);
        salesorder.setDetails(new ArrayList<>());

        for (SalesorderDetailDto detailDto : salesorderDto.getDetails()) {
            //validations and creation on inventory
            var item = itemService.findByCode(detailDto.getItem().getCode());
            if (detailDto.getItem() == null) {
                return ResponseEntity.status(400).body("Item cannot be null.");
            }
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
            double availableStock = inventory.getInQuantity() - inventory.getOutQuantity();

            //creation of salesorder_detail
            var detail = new SalesorderDetail();
            double itemPrice = (detailDto.getItemPrice() != null) ? detailDto.getItemPrice() : item.getPrice();

            detail.setSalesorder(salesorder);
            detail.setItem(item);
            if (detailDto.getOrderQuantity() <= 0) {
                return ResponseEntity.status(404).body("Quantity should be greater than zero.");
            }
            detail.setOrderQuantity(detailDto.getOrderQuantity());
            detail.setStockQuantity(availableStock);
            detail.setItemPrice(itemPrice);
            detail.setAmount(detailDto.getOrderQuantity() * itemPrice);
            salesorder.getDetails().add(detail);
        }
        salesorder = salesorderService.save(salesorder);

//        //update on inventory balance
//        for (SalesorderDetail detail : salesorder.getDetails()) {
//            var inventory = inventoryService.findByItemIdAndItemType(detail.getItem().getId(), detail.getItem().getCategory());
//            inventory.setOutQuantity(inventory.getOutQuantity() + detail.getOrderQuantity());
//            inventoryService.save(inventory);
//        }
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
            if ((inventory.getInQuantity() - inventory.getOutQuantity()) < detailDto.getOrderQuantity()) {
                return ResponseEntity.status(404).body("Cannot deliver more than available quantity.");
            }

            //salesorder details
            if (detailDto.getOrderQuantity() <= 0) {
                return ResponseEntity.status(404).body("Quantity should be greater than zero.");
            }
            detail.setOrderQuantity(detailDto.getOrderQuantity());
            if (detailDto.getItemPrice() != null) {
                detail.setItemPrice(detailDto.getItemPrice()); 
            }
            detail.setAmount(detail.getOrderQuantity() * detail.getItemPrice()); 
            inventory.setOutQuantity(inventory.getOutQuantity() + detailDto.getOrderQuantity());
            inventoryService.save(inventory);
        }
        salesorder = salesorderService.save(salesorder);
        return ResponseEntity.ok(new SalesorderDto(salesorder));
    }
}
