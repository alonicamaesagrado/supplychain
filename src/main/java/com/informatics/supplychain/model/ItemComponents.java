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
    private Item fpCode;
    
    @JoinColumn
    @ManyToOne
    private Item rmCode;
    
    private String quantity;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public ItemComponents() {
        status = StatusEnum.ACTIVE;
    }
 
    public ItemComponents(ItemComponentsDto dto) {
        if (dto.getFpCode() != null) {
            fpCode = new Item(dto.getFpCode());
        }
        if (dto.getRmCode() != null) {
            rmCode = new Item(dto.getRmCode());
        }
        quantity = dto.getQuantity();
    }
}
