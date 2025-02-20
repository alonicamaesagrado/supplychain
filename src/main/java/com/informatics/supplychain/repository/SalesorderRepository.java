package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Salesorder;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesorderRepository extends JpaRepository<Salesorder, Integer> {

    public Salesorder findBySalesorderNo(String salesorderNo);

    List<Salesorder> findByStatus(TransactionStatusEnum status);
    
    List<Salesorder> findByOrderDateBetween(LocalDate fromDate, LocalDate toDate);
    
    List<Salesorder> findByStatusAndOrderDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);
    
    List<Salesorder> findByCustomerId(Integer customerId);
    
    List<Salesorder> findByCustomerIdAndOrderDateBetween(Integer customerId, LocalDate fromDate, LocalDate toDate);
    
    List<Salesorder> findByCustomerIdAndStatusAndOrderDateBetween(Integer customerId, TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT a.salesorder_no FROM salesorder a WHERE a.salesorder_no LIKE CONCAT('SO', :yearMonth, '%') ORDER BY a.salesorder_no DESC LIMIT 1", nativeQuery = true)
    String findLastSalesorderNoByYearMonth(@Param("yearMonth") String yearMonth);

    @Query("SELECT s FROM Salesorder s JOIN s.details d WHERE d.item.id = :itemId")
    List<Salesorder> findByItemId(@Param("itemId") Integer itemId);

}
