package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.service.ItemService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping("v1/item")
    ResponseEntity<?> getItem(@RequestParam String code) {
        var item = itemService.findByCode(code);
        if (item == null) {
            return ResponseEntity.status(404).body("Item not found.");
        }
        if (item.getStatus() != StatusEnum.ACTIVE) {
            return ResponseEntity.status(400).body("Item is inactive.");
        }
        return ResponseEntity.ok(new ItemDto(item));
    }

    @GetMapping("v1/itemList")
    ResponseEntity<List<ItemDto>> getItemList(@RequestParam(required = false) String category, @RequestParam(required = false) StatusEnum status) {
        List<Item> items;

        if ((category != null && !category.isEmpty()) && status != null) {
            items = itemService.findByCategoryAndStatus(category, status);
        } else if (category != null && !category.isEmpty()) {
            items = itemService.findByCategory(category);
        } else if (status != null) {
            items = itemService.findByStatus(status);
        } else {
            items = itemService.findAll();
        }
        List<ItemDto> itemDtos = items.stream().map(ItemDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(itemDtos);
    }

    @PostMapping("v1/item")
    ResponseEntity<ItemDto> saveItem(@RequestBody ItemDto itemDto) {
        var item = new Item();
        item.setCode(itemDto.getCode());
        item.setDescription(itemDto.getDescription());
        item.setCategory(itemDto.getCategory());
        item.setBrand(itemDto.getBrand());
        item.setUnit(itemDto.getUnit());
        item.setReorderPoint(itemDto.getReorderPoint());
        item.setPrice(itemDto.getPrice());
        item.setCost(itemDto.getCost());
        return ResponseEntity.ok(new ItemDto(itemService.save(item)));
    }

    @PutMapping("v1/item/{itemCode}")
    public ResponseEntity<?> updateItem(@PathVariable("itemCode") String code, @RequestBody ItemDto itemDto) throws Exception {
        var existingItem = itemService.findByCode(code);
        if (existingItem == null) {
            return ResponseEntity.status(404).body("Item not found.");
        }
        existingItem.setDescription(itemDto.getDescription());
        existingItem.setCategory(itemDto.getCategory());
        existingItem.setBrand(itemDto.getBrand());
        existingItem.setUnit(itemDto.getUnit());
        existingItem.setReorderPoint(itemDto.getReorderPoint());
        existingItem.setPrice(itemDto.getPrice());
        existingItem.setCost(itemDto.getCost());
        existingItem.setStatus(itemDto.getStatus());

        var updatedItem = itemService.save(existingItem);

        return ResponseEntity.ok(new ItemDto(updatedItem));
    }
}
