package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.StockInDto;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.service.ItemService;
import com.informatics.supplychain.service.StockInService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class StockInController {

    @Autowired
    StockInService stockInService;

    @Autowired
    ItemService itemService;

    @PostMapping("v1/stockIn")
    ResponseEntity<?> saveStockIn(@RequestBody StockInDto stockInDto) throws Exception {
        var stockIn = new StockIn();
        var item = itemService.findByCode(stockInDto.getItem().getCode());

        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = stockInService.getNextSeriesNumber(yearMonth);
        String transactionNo = "STI" + yearMonth + String.format("%04d", series);

        stockIn.setTransactionNo(transactionNo);
        stockIn.setTransactionDate(stockInDto.getTransactionDate());
        stockIn.setRemarks(stockInDto.getRemarks());
        if (item == null) {
            throw new Exception("Item does not exist!");
        }
        stockIn.setItem(item);
        stockIn.setQuantity(stockInDto.getQuantity());
        return ResponseEntity.ok(new StockInDto(stockInService.save(stockIn)));
    }
}
