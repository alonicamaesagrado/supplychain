package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.StockInDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.service.ItemService;
import com.informatics.supplychain.service.StockInService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class StockInController {

    @Autowired
    StockInService stockInService;

    @Autowired
    ItemService itemService;

    @GetMapping("v1/stockIn")
    ResponseEntity<?> getStockIn(@RequestParam String transactionNo) {
        var stockIn = stockInService.findByTransactionNo(transactionNo);

        if (stockIn == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        return ResponseEntity.ok(new StockInDto(stockIn));
    }

    @GetMapping("v1/stockInList")
    ResponseEntity<List<StockInDto>> getStockInList(@RequestParam(required = false) StatusEnum status) {
        List<StockIn> stockIn;

        if (status != null) {
            stockIn = stockInService.findByStatus(status);
        } else {
            stockIn = stockInService.findAll();
        }
        List<StockInDto> stockInDtos = stockIn.stream().map(StockInDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(stockInDtos);
    }

    @GetMapping("v1/stockInList/{itemId}")
    public ResponseEntity<List<StockIn>> getStockInByItemId(@PathVariable Integer itemId) {
        List<StockIn> stockIns = stockInService.findByItemId(itemId);
        return ResponseEntity.ok(stockIns);
    }

    @PostMapping("v1/stockIn")
    ResponseEntity<?> saveStockIn(@RequestBody StockInDto stockInDto) throws Exception {
        if (stockInDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }

        var stockIn = new StockIn();
        var item = itemService.findByCode(stockInDto.getItem().getCode());

        String yearMonth = stockInDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = stockInService.getNextSeriesNumber(yearMonth);
        String transactionNo = "STI" + yearMonth + String.format("%04d", series);

        stockIn.setTransactionNo(transactionNo);
        stockIn.setTransactionDate(stockInDto.getTransactionDate());
        stockIn.setRemarks(stockInDto.getRemarks());
        if (item == null) {
            return ResponseEntity.status(404).body("Item does not exist!");
        }
        stockIn.setItem(item);
        stockIn.setQuantity(stockInDto.getQuantity());
        stockIn.setBatchNo(stockInDto.getBatchNo());
        stockIn.setCreatedDateTime(LocalDateTime.now());
        return ResponseEntity.ok(new StockInDto(stockInService.save(stockIn)));
    }

    @PutMapping("v1/stockIn/{transactionNo}")
    public ResponseEntity<?> updateStockIn(@PathVariable("transactionNo") String transactionNo, @RequestBody StockInDto stockInDto) throws Exception {
        var existingTransaction = stockInService.findByTransactionNo(transactionNo);

        if (existingTransaction == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        existingTransaction.setRemarks(stockInDto.getRemarks());
        existingTransaction.setQuantity(stockInDto.getQuantity());
        existingTransaction.setBatchNo(stockInDto.getBatchNo());
        var updateStockIn = stockInService.save(existingTransaction);
        return ResponseEntity.ok(new StockInDto(updateStockIn));
    }
}
