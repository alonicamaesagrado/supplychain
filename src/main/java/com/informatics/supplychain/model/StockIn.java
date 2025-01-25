package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.StockInDto;
import com.informatics.supplychain.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
public class StockIn {

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
    private Item item;

    private Double quantity;
    private String batchNo;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private String createdBy;
    private LocalDateTime createdDateTime;

    public StockIn() {
        status = StatusEnum.ACTIVE;
        createdDateTime = LocalDateTime.now();
    }

    public StockIn(StockInDto dto) {
        transactionNo = dto.getTransactionNo();
        transactionDate = dto.getTransactionDate();
        remarks = dto.getRemarks();
        if (dto.getItem() != null) {
            item = new Item(dto.getItem());
        }
        quantity = dto.getQuantity();
        batchNo = dto.getBatchNo();
    }
}
