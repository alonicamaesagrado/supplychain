package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.StockOutDto;
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
public class StockOut {

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
    private StockIn stockIn;

    @JoinColumn
    @ManyToOne
    private Item item;

    private Double quantity;
    private String batchNo;

    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;
    private String createdBy;
    private LocalDateTime createdDateTime;

    public StockOut() {
        status = TransactionStatusEnum.DRAFT;
        createdDateTime = LocalDateTime.now();
    }

    public StockOut(StockOutDto dto) {
        transactionNo = dto.getTransactionNo();
        transactionDate = dto.getTransactionDate();
        remarks = dto.getRemarks();
        if (dto.getStockIn() != null) {
            stockIn = new StockIn(dto.getStockIn());
        }
        quantity = dto.getQuantity();
    }
}
