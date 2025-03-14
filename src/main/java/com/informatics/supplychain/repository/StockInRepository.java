package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.StockIn;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockInRepository extends JpaRepository<StockIn, Integer> {

    public StockIn findByTransactionNo(String transactionNo);
    
    List<StockIn> findByItem_Id(Integer itemId);

    List<StockIn> findByStatus(TransactionStatusEnum status);

    List<StockIn> findByTransactionDateBetween(LocalDate fromDate, LocalDate toDate);
    
    List<StockIn> findByStatusAndTransactionDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT s.transaction_no FROM stock_in s WHERE s.transaction_no LIKE CONCAT('STI', :yearMonth, '%') ORDER BY s.transaction_no DESC LIMIT 1", nativeQuery = true)
    String findLastTransactionNoByYearMonth(@Param("yearMonth") String yearMonth);

    List<StockIn> findByItemId(@Param("itemId") Integer itemId);

    List<StockIn> findByItemAndStatus(@Param("itemId") Integer itemId, @Param("status") TransactionStatusEnum status);
    
    List<StockIn> findByItemIdAndStatusAndTransactionDateBetween(@Param("itemId") Integer itemId, TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);
}
