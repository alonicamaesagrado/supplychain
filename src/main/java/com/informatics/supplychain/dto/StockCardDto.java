package com.informatics.supplychain.dto;

import java.time.LocalDate;
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

    public StockCardDto(Integer itemId, LocalDate date, String transactionNo, Double stockIn, Double stockOut, Double runningBalance) {
        this.itemId = itemId;
        this.date = date;
        this.transactionNo = transactionNo;
        this.stockIn = stockIn;
        this.stockOut = stockOut;
        this.runningBalance = runningBalance;
    }
}
