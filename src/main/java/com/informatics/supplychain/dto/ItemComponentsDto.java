package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.ItemComponents;


@Getter
@Setter

public class ItemComponentsDto {
    public ItemDto finishProduct;
    public ItemDto rawMaterial;
    public String quantity;
    public StatusEnum status;
    
    public ItemComponentsDto() {
        
    }
    
    public ItemComponentsDto(ItemComponents entity) {
        if (entity != null) {
            if (entity.getFinishProduct() != null) {
                finishProduct = new ItemDto(entity.getFinishProduct());
            }
            if (entity.getRawMaterial() != null) {
                rawMaterial = new ItemDto(entity.getRawMaterial());
            }
            quantity = entity.getQuantity();
            status = entity.getStatus();
        }
    }
}
