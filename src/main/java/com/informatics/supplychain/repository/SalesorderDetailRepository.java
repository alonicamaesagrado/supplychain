package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.SalesorderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesorderDetailRepository extends JpaRepository<SalesorderDetail, Integer> {
}