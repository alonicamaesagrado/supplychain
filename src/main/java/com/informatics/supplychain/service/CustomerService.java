package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Customer;
import java.util.List;
import org.springframework.stereotype.Service;
import com.informatics.supplychain.repository.CustomerRepository;


@Service
public class CustomerService {

    @Autowired
    CustomerRepository supplierRepository;

    public Customer findByIdAndStatus(Integer id, StatusEnum status) {
        return supplierRepository.findByIdAndStatus(id, status);
    }
    
    public List<Customer> findAll(){
      return supplierRepository.findAll();
    }
    
    public Customer save(Customer supplier){
       return supplierRepository.save(supplier);
    }
}
