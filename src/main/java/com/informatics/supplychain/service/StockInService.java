package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.repository.StockInRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockInService {

    @Autowired
    StockInRepository stockInRepository;

    public StockIn findById(Integer id) {
        return stockInRepository.findById(id).orElse(null);
    }

    public StockIn findByTransactionNo(String transactionNo) {
        return stockInRepository.findByTransactionNo(transactionNo);
    }

    public List<StockIn> findByItemId(Integer itemId) {
        return stockInRepository.findByItemId(itemId);
    }
    
    public List<StockIn> findByItemAndStatus(Integer itemId, TransactionStatusEnum status) {
        return stockInRepository.findByItemAndStatus(itemId, status);
    }
    
    public List<StockIn> findByItemAndStatusAndTransactionDateBetween(Integer itemId, TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate) {
        return stockInRepository.findByItemIdAndStatusAndTransactionDateBetween(itemId, status, fromDate, toDate );
    }

    public List<StockIn> findByStatus(TransactionStatusEnum status) {
        return stockInRepository.findByStatus(status);
    }
    
    public List<StockIn> findByStatusAndTransactionDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate) {
        return stockInRepository.findByStatusAndTransactionDateBetween(status, fromDate, toDate );
    }
    
    public List<StockIn> findByTransactionDateBetween(LocalDate fromDate, LocalDate toDate) {
        return stockInRepository.findByTransactionDateBetween(fromDate, toDate );
    }

    public List<StockIn> findAll() {
        return stockInRepository.findAll();
    }

    public StockIn save(StockIn stockIn) {
        return stockInRepository.save(stockIn);
    }

    public int getNextSeriesNumber(String yearMonth) {
        String lastTransactionNo = stockInRepository.findLastTransactionNoByYearMonth(yearMonth);
        if (lastTransactionNo == null || lastTransactionNo.isEmpty()) {
            return 1;
        }
        String seriesPart = lastTransactionNo.substring(lastTransactionNo.length() - 4);
        int nextSeries = Integer.parseInt(seriesPart) + 1;
        return nextSeries;
    }
}
