package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Salesorder;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter

public class SalesorderDto {

    public String salesorderNo;
    @NotNull(message = "Date cannot be null.")
    public LocalDate orderDate;
    public String remarks;
    public CustomerDto customer;
    public TransactionStatusEnum status;
    private LocalDateTime createdDateTime;
    private List<SalesorderDetailDto> details;

    public SalesorderDto() {

    }

    public SalesorderDto(Salesorder entity) {
        if (entity != null) {
            salesorderNo = entity.getSalesorderNo();
            orderDate = entity.getOrderDate();
            remarks = entity.getRemarks();
            customer = new CustomerDto(entity.getCustomer());
            status = entity.getStatus();
            createdDateTime = entity.getCreatedDateTime();
            if (entity.getDetails() != null) {
                details = entity.getDetails().stream().map(SalesorderDetailDto::new).collect(Collectors.toList());
            }
        }
    }
}
