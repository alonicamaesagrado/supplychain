package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.SalesorderDetailDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class SalesorderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    
    @JoinColumn
    @ManyToOne
    private Salesorder salesorder;
    
    @JoinColumn
    @ManyToOne
    private Item item;
    
    private Double orderQuantity;
    private Double itemPrice;
    private Double amount;

    public SalesorderDetail() {

    }

    public SalesorderDetail(SalesorderDetailDto dto) {
        id = dto.getId();
        if (dto.getSalesorder() != null) {
            salesorder = new Salesorder(dto.getSalesorder());
        }
        if (dto.getItem( )!= null) {
            item = new Item(dto.getItem());
        }
        orderQuantity = dto.getOrderQuantity();
        itemPrice = dto.getItemPrice();
        amount = dto.getAmount();
    }
}
