package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Item;


@Getter
@Setter

public class ItemDto {
    public Integer id;
    public String code;
    public String description;
    public String category;
    public String brand;
    public String unit;
    public Double reorderPoint;
    public Double price;
    public Double cost;
    public Double averageCost;
    public StatusEnum status;
    
    public ItemDto() {
        
    }
    
    public ItemDto(Item entity) {
        if (entity != null) {
            id = entity.getId();
            code = entity.getCode();
            description = entity.getDescription();
            category = entity.getCategory();
            brand = entity.getBrand();
            unit = entity.getUnit();
            reorderPoint = entity.getReorderPoint();
            price = entity.getPrice();
            cost = entity.getCost();
            averageCost = entity.getAverageCost();
            status = entity.getStatus();
        }
    }
}
