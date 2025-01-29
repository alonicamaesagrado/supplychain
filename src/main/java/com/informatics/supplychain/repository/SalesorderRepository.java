package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Salesorder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesorderRepository extends JpaRepository<Salesorder, Integer> {
    
    public Salesorder findBySalesorderNo(String salesorderNo);

    List<Salesorder> findByStatus(TransactionStatusEnum status);

    @Query(value = "SELECT a.salesorder_no FROM salesorder a WHERE a.salesorder_no LIKE CONCAT('SO', :yearMonth, '%') ORDER BY a.salesorder_no DESC LIMIT 1", nativeQuery = true)
    String findLastSalesorderNoByYearMonth(@Param("yearMonth") String yearMonth);
}