package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.StockIn;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class StockInDto {

    public String transactionNo;
    @NotNull(message = "Transaction date cannot be null.")
    public LocalDate transactionDate;
    public String remarks;
    public ItemDto item;
    public Double quantity;
    public Double issuedQuantity;
    public Double returnQuantity;
    public String batchNo;
    public LocalDate expiryDate;
    public TransactionStatusEnum status;
    private LocalDateTime createdDateTime;
    private String createdBy;

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
            issuedQuantity = entity.getIssuedQuantity();
            returnQuantity = entity.getReturnQuantity();
            batchNo = entity.getBatchNo();
            expiryDate = entity.getExpiryDate();
            status = entity.getStatus();
            createdDateTime = entity.getCreatedDateTime();
            createdBy = entity.getCreatedBy();
        }
    }
}
