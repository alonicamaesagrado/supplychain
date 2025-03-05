package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.StockOut;
import com.informatics.supplychain.repository.StockOutRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockOutService {

    @Autowired
    StockOutRepository stockOutRepository;

    public StockOut findById(Integer id) {
        return stockOutRepository.findById(id).orElse(null);
    }

    public StockOut findByTransactionNo(String transactionNo) {
        return stockOutRepository.findByTransactionNo(transactionNo);
    }

    public List<StockOut> findByItemId(Integer itemId) {
        return stockOutRepository.findByItemId(itemId);
    }
    
    public List<StockOut> findByItemAndStatus(Integer itemId, TransactionStatusEnum status) {
        return stockOutRepository.findByItemAndStatus(itemId, status);
    }
    
    public List<StockOut> findByItemAndStatusAndTransactionDateBetween(Integer itemId, TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate) {
        return stockOutRepository.findByItemIdAndStatusAndTransactionDateBetween(itemId, status, fromDate, toDate );
    }

    public List<StockOut> findByStatus(TransactionStatusEnum status) {
        return stockOutRepository.findByStatus(status);
    }
    
    public List<StockOut> findByStatusAndTransactionDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate) {
        return stockOutRepository.findByStatusAndTransactionDateBetween(status, fromDate, toDate );
    }
    
    public List<StockOut> findByTransactionDateBetween(LocalDate fromDate, LocalDate toDate) {
        return stockOutRepository.findByTransactionDateBetween(fromDate, toDate );
    }

    public List<StockOut> findAll() {
        return stockOutRepository.findAll();
    }

    public StockOut save(StockOut stockOut) {
        return stockOutRepository.save(stockOut);
    }

    public int getNextSeriesNumber(String yearMonth) {
        String lastTransactionNo = stockOutRepository.findLastTransactionNoByYearMonth(yearMonth);
        if (lastTransactionNo == null || lastTransactionNo.isEmpty()) {
            return 1;
        }
        String seriesPart = lastTransactionNo.substring(lastTransactionNo.length() - 4);
        int nextSeries = Integer.parseInt(seriesPart) + 1;
        return nextSeries;
    }
}
