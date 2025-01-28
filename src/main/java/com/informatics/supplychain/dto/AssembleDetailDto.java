package com.informatics.supplychain.dto;

import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.AssembleDetail;

@Getter
@Setter

public class AssembleDetailDto {

    public Integer id;
    public AssembleDto assemble;
    public ItemDto rawMaterial;
    public Double usedQuantity;

    public AssembleDetailDto() {

    }

    public AssembleDetailDto(AssembleDetail entity) {
        if (entity != null) {
            id = entity.getId();
            if (entity.getAssemble() != null) {
                assemble = new AssembleDto(entity.getAssemble());
            }
            if (entity.getRawMaterial() != null) {
                rawMaterial = new ItemDto(entity.getRawMaterial());
            }
            usedQuantity = entity.getUsedQuantity();
        }
    }
}
