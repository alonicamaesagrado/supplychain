package com.informatics.supplychain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StockCardDto {

    private Integer itemId;
    private LocalDate date;
    private String transactionNo;
    private Double stockIn;
    private Double stockOut;
    private Double runningBalance;
    private LocalDateTime createdDateTime;

    public StockCardDto(Integer itemId, LocalDate date, String transactionNo, Double stockIn, Double stockOut, Double runningBalance, LocalDateTime createdDateTime) {
        this.itemId = itemId;
        this.date = date;
        this.transactionNo = transactionNo;
        this.stockIn = stockIn;
        this.stockOut = stockOut;
        this.runningBalance = runningBalance;
        this.createdDateTime = createdDateTime;
    }
}
