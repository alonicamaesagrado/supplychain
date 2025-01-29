package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.InventoryDto;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@CrossOrigin
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("v1/inventory/{itemId}")
    public ResponseEntity<?> getInventoryByItemId(@PathVariable Integer itemId) {
        List<Inventory> inventory = inventoryService.findByItemId(itemId);
        if (inventory == null || inventory.isEmpty()) {
            return ResponseEntity.status(404).body("Item not found in inventory.");
        }
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("v1/inventoryList")
    ResponseEntity<List<InventoryDto>> getItemList(@RequestParam(required = false) String itemType) {
        List<Inventory> inventory;

        if (itemType != null && !itemType.isEmpty()) {
            inventory = inventoryService.findByItemType(itemType);
        } else {
            inventory = inventoryService.findAll();
        }
        List<InventoryDto> inventoryDtos = inventory.stream().map(InventoryDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDtos);
    }
}
