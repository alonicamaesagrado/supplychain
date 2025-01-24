package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.ItemComponents;


@Getter
@Setter

public class ItemComponentsDto {
    public ItemDto fpCode;
    public ItemDto rmCode;
    public String quantity;
    public StatusEnum status;
    
    public ItemComponentsDto() {
        
    }
    
    public ItemComponentsDto(ItemComponents entity) {
        if (entity != null) {
            if (entity.getFpCode() != null) {
                fpCode = new ItemDto(entity.getFpCode());
            }
            if (entity.getRmCode() != null) {
                rmCode = new ItemDto(entity.getRmCode());
            }
            quantity = entity.getQuantity();
            status = entity.getStatus();
        }
    }
}
