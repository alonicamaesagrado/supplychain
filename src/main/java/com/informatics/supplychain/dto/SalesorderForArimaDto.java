package com.informatics.supplychain.dto;

import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.Salesorder;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

public class SalesorderForArimaDto {

    public LocalDate orderDate;
     private List<SalesorderDetailForArimaDto> salesorderDetailReportArimaDto;

    public SalesorderForArimaDto() {

    }

    public SalesorderForArimaDto(Salesorder salesorder, List<SalesorderDetailForArimaDto> salesorderDetailReportArimaDto) {
        if (salesorder != null) {
            orderDate = salesorder.getOrderDate();
            this.salesorderDetailReportArimaDto = salesorderDetailReportArimaDto;
        }
    }
}
