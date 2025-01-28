package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Assemble;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class AssembleDto {

    public String transactionNo;
    @NotNull(message = "Transaction date cannot be null.")
    public LocalDate transactionDate;
    public String remarks;
    public ItemDto finishProduct;
    public Double assemble_quantity;
    public String batchNo;
    public TransactionStatusEnum status;
    private LocalDateTime createdDateTime;
    private List<AssembleDetailDto> details;

    public AssembleDto() {

    }

    public AssembleDto(Assemble entity) {
        if (entity != null) {
            transactionNo = entity.getTransactionNo();
            transactionDate = entity.getTransactionDate();
            remarks = entity.getRemarks();
            if (entity.getFinishProduct()!= null) {
                finishProduct = new ItemDto(entity.getFinishProduct());
            }
            assemble_quantity = entity.getAssemble_quantity();
            batchNo = entity.getBatchNo();
            status = entity.getStatus();
            createdDateTime = entity.getCreatedDateTime();
        }
    }
}
