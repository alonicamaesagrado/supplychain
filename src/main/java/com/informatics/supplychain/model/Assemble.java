package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.AssembleDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class Assemble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    private String transactionNo;
    @NotNull(message = "Transaction date cannot be null.")
    private LocalDate transactionDate;
    private String remarks;

    @JoinColumn
    @ManyToOne
    private Item finishProduct;

    private Double assemble_quantity;
    private String batchNo;

    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;
    private String createdBy;
    private LocalDateTime createdDateTime;

    public Assemble() {
        status = TransactionStatusEnum.DRAFT;
        createdDateTime = LocalDateTime.now();
    }

    public Assemble(AssembleDto dto) {
        transactionNo = dto.getTransactionNo();
        transactionDate = dto.getTransactionDate();
        remarks = dto.getRemarks();
        if (dto.getFinishProduct()!= null) {
            finishProduct = new Item(dto.getFinishProduct());
        }
        assemble_quantity = dto.getAssemble_quantity();
        batchNo = dto.getBatchNo();
    }
}
