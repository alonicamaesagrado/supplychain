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
    public StockInDto stockIn;
    public AssembleDto assemble;
    public Double quantity;
    public String batchNo;
    public String transactionType;
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
            if (entity.getStockIn() != null) {
                stockIn = new StockInDto(entity.getStockIn());
            }
            if (entity.getAssemble() != null) {
                assemble = new AssembleDto(entity.getAssemble());
            }
            quantity = entity.getQuantity();
            batchNo = entity.getBatchNo();
            transactionType = entity.getTransactionType();
            status = entity.getStatus();
            createdDateTime = entity.getCreatedDateTime();
            createdBy = entity.getCreatedBy();
        }
    }
}
