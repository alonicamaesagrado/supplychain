package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.ItemComponentsDto;
import com.informatics.supplychain.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ItemComponents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    
    @JoinColumn
    @ManyToOne
    private Item finishProduct;
    
    
    @JoinColumn
    @ManyToOne
    private Item rawMaterial;
    
    private String quantity;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public ItemComponents() {
        status = StatusEnum.ACTIVE;
    }
 
    public ItemComponents(ItemComponentsDto dto) {
        if (dto.getFinishProduct() != null) {
            finishProduct = new Item(dto.getFinishProduct());
        }
        if (dto.getRawMaterial()!= null) {
            rawMaterial = new Item(dto.getRawMaterial());
        }
        quantity = dto.getQuantity();
    }
}
