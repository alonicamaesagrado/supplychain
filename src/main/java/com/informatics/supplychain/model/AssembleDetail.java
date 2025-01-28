package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.AssembleDetailDto;
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
public class AssembleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    
    @JoinColumn
    @ManyToOne
    private Assemble assemble;
    
    @JoinColumn
    @ManyToOne
    private Item rawMaterial;
    
    private Double usedQuantity;

    public AssembleDetail() {

    }

    public AssembleDetail(AssembleDetailDto dto) {
        id = dto.getId();
        if (dto.getAssemble() != null) {
            assemble = new Assemble(dto.getAssemble());
        }
        if (dto.getRawMaterial() != null) {
            rawMaterial = new Item(dto.getRawMaterial());
        }
        usedQuantity = dto.getUsedQuantity();
    }
}
