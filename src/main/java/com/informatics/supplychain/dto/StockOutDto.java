package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.StockOut;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class StockOutDto {

    public String transactionNo;
    @NotNull(message = "Transaction date cannot be null.")
    public LocalDate transactionDate;
    public String remarks;
    public ItemDto item;
    public Double quantity;
    public String batchNo;
    public TransactionStatusEnum status;
    private LocalDateTime createdDateTime;
    private String createdBy;

    public StockOutDto() {

    }

    public StockOutDto(StockOut entity) {
        if (entity != null) {
            transactionNo = entity.getTransactionNo();
            transactionDate = entity.getTransactionDate();
            remarks = entity.getRemarks();
            if (entity.getItem( )!= null) {
                item = new ItemDto(entity.getItem());
            }
            quantity = entity.getQuantity();
            batchNo = entity.getBatchNo();
            status = entity.getStatus();
            createdDateTime = entity.getCreatedDateTime();
            createdBy = entity.getCreatedBy();
        }
    }
}
