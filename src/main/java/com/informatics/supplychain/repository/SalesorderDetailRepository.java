package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.SalesorderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesorderDetailRepository extends JpaRepository<SalesorderDetail, Integer> {

    List<SalesorderDetail> findByItem_Id(Integer itemId);
}
