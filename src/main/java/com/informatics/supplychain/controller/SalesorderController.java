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

    @PostMapping("v1/salesorder")
    public ResponseEntity<?> saveSalesorder(@RequestBody SalesorderDto salesorderDto) throws Exception {
        if (salesorderDto.getOrderDate() == null) {
            return ResponseEntity.status(400).body("Order date cannot be null.");
        }

        // Creating salesorder
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

        //validations and creation of inventory
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

            //creation of salesorder detail
            var detail = new SalesorderDetail();
            detail.setSalesorder(salesorder);
            detail.setItem(item);
            detail.setOrderQuantity(detailDto.getOrderQuantity());
            detail.setItemPrice(item.getPrice());
            detail.setAmount(detailDto.getOrderQuantity() * item.getPrice());
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

}
