package com.informatics.supplychain.dto;

import com.informatics.supplychain.model.Inventory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class InventoryDto {

    public Integer id;
    public ItemDto item;
    public String itemType;
    public Double inQuantity;
    public Double outQuantity;

    public InventoryDto() {

    }

    public InventoryDto(Inventory entity) {
        if (entity != null) {
            id = entity.getId();
            if (entity.getItem() != null) {
                item = new ItemDto(entity.getItem());
            }
            itemType = entity.getItemType();
            inQuantity = entity.getInQuantity();
            outQuantity = entity.getOutQuantity();
        }
    }
}
