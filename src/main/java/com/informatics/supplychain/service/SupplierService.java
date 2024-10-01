package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Supplier;
import com.informatics.supplychain.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class SupplierService {

    @Autowired
    SupplierRepository supplierRepository;

    public Supplier findByCodeAndStatus(String code, StatusEnum status) {
        return supplierRepository.findByCodeAndStatus(code, status);
    }
    
    public List<Supplier> findAll(){
      return supplierRepository.findAll();
    }
    
    public Supplier save(Supplier supplier){
       return supplierRepository.save(supplier);
    }
}
