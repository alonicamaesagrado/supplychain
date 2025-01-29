package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.CustomerDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Customer;
import com.informatics.supplychain.service.CustomerService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class CustomerController {
    @Autowired
    CustomerService customerService;
    
    @GetMapping("v1/customer")
    ResponseEntity<CustomerDto> getCustomer(@RequestParam Integer id) {
        var customer = customerService.findByIdAndStatus(id, StatusEnum.ACTIVE);
        return ResponseEntity.ok(new CustomerDto(customer));
    } 
    
    @GetMapping("v1/customerList")
    ResponseEntity<List<CustomerDto>> getCustomerList() {
        return ResponseEntity.ok(customerService.findAll().stream().map(e -> new CustomerDto(e)).collect(Collectors.toList()));
    } 
    
    @PostMapping("v1/customer")
    ResponseEntity<CustomerDto> saveCustomer(@RequestBody CustomerDto customerDto) {
        var customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        customer.setContactPerson(customerDto.getContactPerson());
        customer.setContactNumber(customerDto.getContactNumber());
        customer.setStatus(customerDto.getStatus());
        return ResponseEntity.ok(new CustomerDto(customerService.save(customer)));
    }
}
