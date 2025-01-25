package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.ItemComponentsDto;
import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.ItemComponents;
import com.informatics.supplychain.service.ItemComponentsService;
import com.informatics.supplychain.service.ItemService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    ItemComponentsService itemComponentsService;

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

    @GetMapping("v1/item-with-components/{finishProductId}")
    ResponseEntity<?> getItemWithComponents(@PathVariable Integer finishProductId) {
        Item finishProduct = itemService.findById(finishProductId);
        if (finishProduct == null) {
            return ResponseEntity.status(404).body("Finish product with ID " + finishProductId + " not found.");
        }
        List<ItemComponents> components = itemComponentsService.findByFinishProduct(finishProduct);

        List<ItemComponentsDto.Component> componentDtos = components.stream().map(component -> {
            return new ItemComponentsDto.Component(
                    new ItemDto(component.getRawMaterial()),
                    component.getQuantity());
        }).toList();

        ItemComponentsDto itemComponentsDto = new ItemComponentsDto();
        itemComponentsDto.setFinishProduct(new ItemDto(finishProduct));
        itemComponentsDto.setComponents(componentDtos);
        itemComponentsDto.setStatus(finishProduct.getStatus());
        return ResponseEntity.ok(itemComponentsDto);
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
    ResponseEntity<?> saveItem(@RequestBody ItemDto itemDto) {
        Item existingItem = itemService.findByCode(itemDto.getCode());
        if (existingItem != null) {
            return ResponseEntity.status(400).body("Item code already exists.");
        }
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

    @PostMapping("v1/item-with-components")
    ResponseEntity<?> saveItemWithComponents(@RequestBody ItemComponentsDto itemComponentsDto) {

        //validations
        Item existingItem = itemService.findByCode(itemComponentsDto.getFinishProduct().getCode());
        if (existingItem != null) {
            return ResponseEntity.status(400).body("Finish product code already exists.");
        }

        for (ItemComponentsDto.Component componentDto : itemComponentsDto.getComponents()) {
            Item rawMaterial = itemService.findByCode(componentDto.getRawMaterial().getCode());
            if (rawMaterial == null) {
                return ResponseEntity.status(400).body("Raw material code " + componentDto.getRawMaterial().getCode() + " does not exist!");
            }
        }

        //creation of main item
        var finishProduct = new Item();
        finishProduct.setCode(itemComponentsDto.getFinishProduct().getCode());
        finishProduct.setDescription(itemComponentsDto.getFinishProduct().getDescription());
        finishProduct.setCategory(itemComponentsDto.getFinishProduct().getCategory());
        finishProduct.setBrand(itemComponentsDto.getFinishProduct().getBrand());
        finishProduct.setUnit(itemComponentsDto.getFinishProduct().getUnit());
        finishProduct.setReorderPoint(itemComponentsDto.getFinishProduct().getReorderPoint());
        finishProduct.setPrice(itemComponentsDto.getFinishProduct().getPrice());
        finishProduct.setCost(itemComponentsDto.getFinishProduct().getCost());
        Item savedFinishProduct = itemService.save(finishProduct);

        //creation of raw materials
        for (ItemComponentsDto.Component componentDto : itemComponentsDto.getComponents()) {
            Item rawMaterial = itemService.findByCode(componentDto.getRawMaterial().getCode());
            ItemComponents itemComponent = new ItemComponents();
            itemComponent.setFinishProduct(savedFinishProduct);
            itemComponent.setRawMaterial(rawMaterial);
            itemComponent.setQuantity(componentDto.getQuantity());
            itemComponentsService.save(itemComponent);
        }

        //response
        ItemComponentsDto responseDto = new ItemComponentsDto();
        responseDto.setFinishProduct(new ItemDto(savedFinishProduct));
        responseDto.setComponents(itemComponentsDto.getComponents());
        responseDto.setStatus(StatusEnum.ACTIVE);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("v1/items/mass-upload")
    ResponseEntity<?> saveItems(@RequestBody List<ItemDto> itemDtos) {
        List<String> errors = new ArrayList<>();
        List<ItemDto> savedItems = new ArrayList<>();

        for (ItemDto itemDto : itemDtos) {
            Item existingItem = itemService.findByCode(itemDto.getCode());
            if (existingItem != null) {
                errors.add("Item with code " + itemDto.getCode() + " already exists.");
                continue;
            }
            var item = new Item();
            item.setCode(itemDto.getCode());
            item.setDescription(itemDto.getDescription());
            item.setCategory(itemDto.getCategory());
            item.setBrand(itemDto.getBrand());
            item.setUnit(itemDto.getUnit());
            item.setReorderPoint(itemDto.getReorderPoint());
            item.setPrice(itemDto.getPrice());
            item.setCost(itemDto.getCost());
            savedItems.add(new ItemDto(itemService.save(item)));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("savedItems", savedItems);
        response.put("errors", errors);

        return ResponseEntity.ok(response);
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
