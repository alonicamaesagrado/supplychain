package com.informatics.supplychain.dto;

import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.SalesorderDetail;

@Getter
@Setter

public class SalesorderDetailForArimaDto {

    public Integer item;
    public Double orderQuantity;

    public SalesorderDetailForArimaDto() {

    }

    public SalesorderDetailForArimaDto(SalesorderDetail salesorderDetails) {
        if (salesorderDetails != null) {
            item = salesorderDetails.getItem().getId();
            orderQuantity = salesorderDetails.getOrderQuantity();
        }
    }
}
