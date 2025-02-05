package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Assemble;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssembleRepository extends JpaRepository<Assemble, Integer> {

    public Assemble findByTransactionNo(String transactionNo);

    @Query("SELECT a FROM Assemble a WHERE a.finishProduct.id = :itemId")
    List<Assemble> findByFinishProductId(@Param("itemId") Integer itemId);

    List<Assemble> findByFinishProductAndStatus(Integer finishProduct, TransactionStatusEnum status);

    List<Assemble> findByStatus(TransactionStatusEnum status);

    @Query(value = "SELECT a.transaction_no FROM assemble a WHERE a.transaction_no LIKE CONCAT('AS', :yearMonth, '%') ORDER BY a.transaction_no DESC LIMIT 1", nativeQuery = true)
    String findLastTransactionNoByYearMonth(@Param("yearMonth") String yearMonth);
}
