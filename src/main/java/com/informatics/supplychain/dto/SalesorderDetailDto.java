package com.informatics.supplychain.dto;

import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.SalesorderDetail;

@Getter
@Setter

public class SalesorderDetailDto {

    public Integer id;
    public SalesorderDto salesorder;
    public ItemDto item;
    public Double orderQuantity;
    public Double itemPrice;
    public Double amount;

    public SalesorderDetailDto() {

    }

    public SalesorderDetailDto(SalesorderDetail entity) {
        if (entity != null) {
            id = entity.getId();
            if (entity.getSalesorder() != null) {
                salesorder = new SalesorderDto(entity.getSalesorder());
            }
            if (entity.getItem() != null) {
                item = new ItemDto(entity.getItem());
            }
            orderQuantity = entity.getOrderQuantity();
            itemPrice = entity.getItemPrice();
            amount = entity.getAmount();
        }
    }
}
