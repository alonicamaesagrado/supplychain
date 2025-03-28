package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.StockOutDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.model.StockOut;
import com.informatics.supplychain.service.AssembleService;
import com.informatics.supplychain.service.InventoryService;
import com.informatics.supplychain.service.ItemService;
import com.informatics.supplychain.service.StockInService;
import com.informatics.supplychain.service.StockOutService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class StockOutController extends BaseController {

    @Autowired
    StockOutService stockOutService;

    @Autowired
    ItemService itemService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    StockInService stockInService;

    @Autowired
    AssembleService assembleService;

    @GetMapping("v1/stockOut")
    ResponseEntity<?> getStockOut(@RequestParam String transactionNo) {
        var stockOut = stockOutService.findByTransactionNo(transactionNo);

        if (stockOut == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        return ResponseEntity.ok(new StockOutDto(stockOut));
    }

    @GetMapping("v1/stockOutList")
    ResponseEntity<List<StockOutDto>> getStockOutList(@RequestParam(required = false) TransactionStatusEnum status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<StockOut> stockOut;
        if (fromDate != null && toDate != null) {
            stockOut = (status != null) ? stockOutService.findByStatusAndTransactionDateBetween(status, fromDate, toDate) : stockOutService.findByTransactionDateBetween(fromDate, toDate);
        } else {
            stockOut = (status != null) ? stockOutService.findByStatus(status) : stockOutService.findAll();
        }
        List<StockOutDto> stockOutDtos = stockOut.stream().map(StockOutDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(stockOutDtos);
    }

    @GetMapping("v1/stockOutList/{itemId}")
    public ResponseEntity<List<StockOut>> getStockOutByItemId(@PathVariable Integer itemId, @RequestParam(required = false) TransactionStatusEnum status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<StockOut> stockOuts = stockOutService.findByItemId(itemId);
        if (fromDate != null && toDate != null) {
            stockOuts = (status != null) ? stockOutService.findByItemAndStatusAndTransactionDateBetween(itemId, status, fromDate, toDate) : stockOutService.findByItemAndTransactionDateBetween(itemId, fromDate, toDate);
        } else {
            stockOuts = (status != null) ? stockOutService.findByItemAndStatus(itemId, status) : stockOutService.findByItemId(itemId);
        }
        return ResponseEntity.ok(stockOuts);
    }

    @PostMapping("v1/stockout-rawmats")
    ResponseEntity<?> saveRawMatsStockOut(@RequestHeader String usercode, @RequestHeader String token, @RequestBody StockOutDto stockOutDto) throws Exception {
        if (!verify(usercode, token)) {
            return ResponseEntity.badRequest().build();
        }
        if (stockOutDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }

        //creation of stock out
        var stockOut = new StockOut();
        var stockIn = stockInService.findByTransactionNo(stockOutDto.getStockIn().getTransactionNo());

        String yearMonth = stockOutDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = stockOutService.getNextSeriesNumberForRawMats(yearMonth);
        String transactionNo = "STO-RM-" + yearMonth + String.format("%04d", series);

        stockOut.setTransactionNo(transactionNo);
        stockOut.setTransactionDate(stockOutDto.getTransactionDate());
        stockOut.setRemarks(stockOutDto.getRemarks());
        if (stockIn == null) {
            return ResponseEntity.status(404).body("Stock-In transaction does not exist!");
        }
        stockOut.setStockIn(stockIn);
        stockOut.setItem(stockIn.getItem());
        if (stockOutDto.getQuantity() <= 0) {
            return ResponseEntity.status(404).body("Quantity should be greater than zero.");
        }
        if ((stockIn.getQuantity() - stockIn.getIssuedQuantity() - stockIn.getReturnQuantity()) < stockOutDto.getQuantity()) {
            return ResponseEntity.status(404).body("Return quantity is greater than available.");
        }
        stockOut.setQuantity(stockOutDto.getQuantity());
        stockOut.setBatchNo(stockIn.getBatchNo());
        stockOut.setTransactionType("RawMats");
        stockOut.setCreatedDateTime(LocalDateTime.now());
        stockOut.setCreatedBy(usercode);
        stockOut = stockOutService.save(stockOut);
        return ResponseEntity.ok(new StockOutDto(stockOut));
    }

    @PostMapping("v1/stockout-product")
    ResponseEntity<?> saveProductStockOut(@RequestHeader String usercode, @RequestHeader String token, @RequestBody StockOutDto stockOutDto) throws Exception {
        if (!verify(usercode, token)) {
            return ResponseEntity.badRequest().build();
        }
        if (stockOutDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }

        //creation of stock out
        var stockOut = new StockOut();
        var assemble = assembleService.findByTransactionNo(stockOutDto.getAssemble().getTransactionNo());

        String yearMonth = stockOutDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = stockOutService.getNextSeriesNumberForProduct(yearMonth);
        String transactionNo = "STO-FP-" + yearMonth + String.format("%04d", series);

        stockOut.setTransactionNo(transactionNo);
        stockOut.setTransactionDate(stockOutDto.getTransactionDate());
        stockOut.setRemarks(stockOutDto.getRemarks());
        if (assemble == null) {
            return ResponseEntity.status(404).body("Assemble transaction does not exist!");
        }
        stockOut.setAssemble(assemble);
        stockOut.setItem(assemble.getFinishProduct());
        if (stockOutDto.getQuantity() <= 0) {
            return ResponseEntity.status(404).body("Quantity should be greater than zero.");
        }
        if ((assemble.getAssembleQuantity() - assemble.getIssuedQuantity() - assemble.getReturnQuantity()) < stockOutDto.getQuantity()) {
            return ResponseEntity.status(404).body("Return quantity is greater than available.");
        }
        stockOut.setQuantity(stockOutDto.getQuantity());
        stockOut.setBatchNo(assemble.getBatchNo());
        stockOut.setTransactionType("Product");
        stockOut.setCreatedDateTime(LocalDateTime.now());
        stockOut.setCreatedBy(usercode);
        stockOut = stockOutService.save(stockOut);
        return ResponseEntity.ok(new StockOutDto(stockOut));
    }

    @PutMapping("v1/stockOut/{transactionNo}")
    public ResponseEntity<?> updateStockOut(@PathVariable("transactionNo") String transactionNo, @RequestBody StockOutDto stockOutDto) throws Exception {
        var existingTransaction = stockOutService.findByTransactionNo(transactionNo);

        //validations
        if (existingTransaction == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        if (TransactionStatusEnum.COMPLETED.equals(existingTransaction.getStatus())) {
            return ResponseEntity.status(400).body("Cannot edit completed transactions!");
        }
        if (stockOutDto.getQuantity() <= 0) {
            return ResponseEntity.status(404).body("Quantity should be greater than zero.");
        }

        var stockIn = existingTransaction.getStockIn() != null ? stockInService.findByTransactionNo(existingTransaction.getStockIn().getTransactionNo()) : null;
        var assemble = existingTransaction.getAssemble() != null ? assembleService.findByTransactionNo(existingTransaction.getAssemble().getTransactionNo()) : null;

        if ("RawMats".equals(existingTransaction.getTransactionType())) {
            if ((stockIn.getQuantity() - stockIn.getIssuedQuantity() - stockIn.getReturnQuantity()) < stockOutDto.getQuantity()) {
                return ResponseEntity.status(404).body("Return quantity is greater than available.");
            }
        } else if ("Product".equals(existingTransaction.getTransactionType())) {
            if ((assemble.getAssembleQuantity() - assemble.getIssuedQuantity() - assemble.getReturnQuantity()) < stockOutDto.getQuantity()) {
                return ResponseEntity.status(404).body("Return quantity is greater than available.");
            }
        }

        //code if status is completed then update inventory
        var item = itemService.findByCode(existingTransaction.getItem().getCode());
        if (TransactionStatusEnum.COMPLETED.equals(stockOutDto.getStatus())) {
            var inventory = inventoryService.findByItemIdAndItemType(item.getId(), item.getCategory());
            double updatedQuantity = stockOutDto.getQuantity() == null ? existingTransaction.getQuantity() : stockOutDto.getQuantity();
            if (inventory == null) {
                inventory = new Inventory();
                inventory.setItem(item);
                inventory.setItemType(item.getCategory());
                inventory.setInQuantity(0.0);
                inventory.setOutQuantity(stockOutDto.getQuantity());
            } else {
                inventory.setOutQuantity(inventory.getOutQuantity() + updatedQuantity);
            }
            inventoryService.save(inventory);
        }
        //update stock out details
        existingTransaction.setRemarks(stockOutDto.getRemarks());
        existingTransaction.setQuantity(stockOutDto.getQuantity() == null ? existingTransaction.getQuantity() : stockOutDto.getQuantity());
        existingTransaction.setStatus(stockOutDto.getStatus());

        //update return qty on stock in
        if (stockIn != null) {
            stockIn.setReturnQuantity(stockIn.getReturnQuantity() + stockOutDto.getQuantity());
            stockInService.save(stockIn);
        }
        //update return qty on assemble
        if (assemble != null) {
            assemble.setReturnQuantity(assemble.getReturnQuantity() + stockOutDto.getQuantity());
            assembleService.save(assemble);
        }

        existingTransaction = stockOutService.save(existingTransaction);
        return ResponseEntity.ok(new StockOutDto(existingTransaction));
    }
}
