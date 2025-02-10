package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.model.AssembleDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssembleDetailRepository extends JpaRepository<AssembleDetail, Integer> {
    
    List<AssembleDetail> findByAssemble(Assemble assemble);
}