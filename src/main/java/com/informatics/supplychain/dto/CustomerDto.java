package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Customer;


@Getter
@Setter

public class CustomerDto {
    public Integer id;
    public String name;
    public String address;
    public String contactPerson;
    public String contactNumber;
    public StatusEnum status;
    
    public CustomerDto() {
        
    }
    
    public CustomerDto(Customer customer) {
        if (customer != null) {
            id = customer.getId();
            name = customer.getName();
            address = customer.getAddress();
            contactPerson = customer.getContactPerson();
            contactNumber = customer.getContactNumber();
            status = customer.getStatus();
        }
    }
}
