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
import java.time.LocalDate;
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
    private LocalDate transactionDate;
    private String remarks;
  
    @JoinColumn
    @ManyToOne
    private Item item;
    
    private Double quantity;
    
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public StockIn() {
        status = StatusEnum.ACTIVE;
    }
 
    public StockIn(StockInDto dto) {
        transactionNo = dto.getTransactionNo();
        transactionDate = dto.getTransactionDate();
        remarks = dto.getRemarks();
        if (dto.getItem() != null) {
            item = new Item(dto.getItem());
        }
        quantity = dto.getQuantity();
    }
}
