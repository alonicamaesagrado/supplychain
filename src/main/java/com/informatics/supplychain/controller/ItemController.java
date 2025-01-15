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
import org.springframework.web.bind.annotation.PostMapping;
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
        var item = itemService.findByCodeAndStatus(code, StatusEnum.ACTIVE);
        if (item == null) {
            return ResponseEntity.status(404).body("Item not found.");
        }
        return ResponseEntity.ok(new ItemDto(item));
    }

    @GetMapping("v1/itemList")
    ResponseEntity<List<ItemDto>> getItemList(@RequestParam(required = false) String category) {
        List<Item> items;

        if (category != null && !category.isEmpty()) {
            items = itemService.findByCategory(category);
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
}
