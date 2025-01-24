package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.StockIn;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockInRepository extends JpaRepository<StockIn, Integer>{
    
    public StockIn findByTransactionNoAndStatus(String transactionNo, StatusEnum status);
    
    List<StockIn> findByStatus(StatusEnum status);
    
    @Query("SELECT MAX(CAST(SUBSTRING(si.transactionNo, 7, 4) AS int)) FROM StockIn si WHERE si.transactionNo LIKE :yearMonth%")
    Integer findMaxSeriesForYearMonth(@Param("yearMonth") String yearMonth);
}
