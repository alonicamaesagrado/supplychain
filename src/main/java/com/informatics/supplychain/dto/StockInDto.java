package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.StockIn;
import java.time.LocalDate;

@Getter
@Setter

public class StockInDto {

    public String transactionNo;
    public LocalDate transactionDate;
    public String remarks;
    public ItemDto item;
    public Double quantity;
    public StatusEnum status;

    public StockInDto() {

    }

    public StockInDto(StockIn entity) {
        if (entity != null) {
            transactionNo = entity.getTransactionNo();
            transactionDate = entity.getTransactionDate();
            remarks = entity.getRemarks();
            if (entity.getItem( )!= null) {
                item = new ItemDto(entity.getItem());
            }
            quantity = entity.getQuantity();
            status = entity.getStatus();
        }
    }
}
