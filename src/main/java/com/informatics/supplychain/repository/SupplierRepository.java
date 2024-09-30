package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author nica
 */
public interface SupplierRepository extends JpaRepository<Supplier, Integer>{
    
    
    public Supplier findByCodeAndStatus(String code, StatusEnum status);
}
