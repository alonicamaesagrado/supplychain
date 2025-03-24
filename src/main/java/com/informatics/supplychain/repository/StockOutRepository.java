package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.StockOut;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockOutRepository extends JpaRepository<StockOut, Integer> {

    public StockOut findByTransactionNo(String transactionNo);

    List<StockOut> findByStatus(TransactionStatusEnum status);

    List<StockOut> findByTransactionDateBetween(LocalDate fromDate, LocalDate toDate);
    
    List<StockOut> findByStatusAndTransactionDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT s.transaction_no FROM stock_out s WHERE s.transaction_no LIKE CONCAT('STO-RM-', :yearMonth, '%') ORDER BY s.transaction_no DESC LIMIT 1", nativeQuery = true)
    String findLastTransactionNoForRawMatsByYearMonth(@Param("yearMonth") String yearMonth);
    
    @Query(value = "SELECT s.transaction_no FROM stock_out s WHERE s.transaction_no LIKE CONCAT('STO-FP-', :yearMonth, '%') ORDER BY s.transaction_no DESC LIMIT 1", nativeQuery = true)
    String findLastTransactionNoForProductByYearMonth(@Param("yearMonth") String yearMonth);

    List<StockOut> findByItemId(@Param("itemId") Integer itemId);

    List<StockOut> findByItemAndStatus(@Param("itemId") Integer itemId, @Param("status") TransactionStatusEnum status);
    
    List<StockOut> findByItemIdAndStatusAndTransactionDateBetween(@Param("itemId") Integer itemId, TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);
    
    List<StockOut> findByItemIdAndTransactionDateBetween(@Param("itemId") Integer itemId, LocalDate fromDate, LocalDate toDate);
}
