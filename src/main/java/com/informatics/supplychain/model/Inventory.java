package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.InventoryDto;
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
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    
    @JoinColumn
    @ManyToOne
    private Item item;
    
    private String itemType;
    private Double inQuantity;
    private Double outQuantity;
    
    public Inventory() {
    }
 
    public Inventory(InventoryDto dto) {
        id = dto.getId();
        if (dto.getItem() != null) {
            item = new Item(dto.getItem());
        }
        itemType = dto.getItemType();
        inQuantity = dto.getInQuantity();
        outQuantity = dto.getOutQuantity();
    }
}
