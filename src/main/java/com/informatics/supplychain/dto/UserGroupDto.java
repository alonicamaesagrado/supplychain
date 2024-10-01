package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Supplier;

/**
 *
 * @author nica
 */

@Getter
@Setter

public class SupplierDto {
    public String code;
    public String name;
    public String address;
    public String company;
    public StatusEnum status;
    
    public SupplierDto() {
        
    }
    
    public SupplierDto(Supplier supplier) {
        if (supplier != null) {
            code = supplier.getCode();
            name = supplier.getName();
            address = supplier.getAddress();
            company = supplier.getCompany();
            status = supplier.getStatus();
        }
    }
}
