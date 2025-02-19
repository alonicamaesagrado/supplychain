package com.informatics.supplychain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.SalesorderDetail;

@Getter
@Setter

public class SalesorderDetailDto {

    public Integer id;

    @JsonIgnore
    private SalesorderDto salesorder;

    public ItemDto item;
    public Double orderQuantity;
    public Double stockQuantity;
    public Double itemPrice;
    public Double amount;

    public SalesorderDetailDto() {

    }

    public SalesorderDetailDto(SalesorderDetail entity) {
        if (entity != null) {
            id = entity.getId();
            item = new ItemDto(entity.getItem());
            orderQuantity = entity.getOrderQuantity();
            stockQuantity = entity.getStockQuantity();
            itemPrice = entity.getItemPrice();
            amount = entity.getAmount();
        }
    }
}
