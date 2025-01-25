package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.StockIn;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockInRepository extends JpaRepository<StockIn, Integer> {

    public StockIn findByTransactionNo(String transactionNo);

    List<StockIn> findByStatus(StatusEnum status);

    @Query(value = "SELECT s.transaction_no FROM stock_in s WHERE s.transaction_no LIKE CONCAT('STI', :yearMonth, '%') ORDER BY s.transaction_no DESC LIMIT 1", nativeQuery = true)
    String findLastTransactionNoByYearMonth(@Param("yearMonth") String yearMonth);
    
    List<StockIn> findByItemId(@Param("itemId") Integer itemId);
    
    List<StockIn> findByItemAndStatus(@Param("itemId") Integer itemId, @Param("status") StatusEnum status);
}
