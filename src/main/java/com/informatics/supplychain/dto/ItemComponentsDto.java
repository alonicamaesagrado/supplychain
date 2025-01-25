package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.ItemComponents;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemComponentsDto {
    public ItemDto finishProduct;
    public List<Component> components;
    public StatusEnum status;

    public ItemComponentsDto() {
        this.components = new ArrayList<>();
    }

    public ItemComponentsDto(ItemComponents entity) {
        this();
        if (entity != null) {
            if (entity.getFinishProduct() != null) {
                finishProduct = new ItemDto(entity.getFinishProduct());
            }
            if (entity.getRawMaterial() != null) {
                components.add(new Component(new ItemDto(entity.getRawMaterial()), entity.getQuantity()));
            }
            status = entity.getStatus();
        }
    }

    @Getter
    @Setter
    public static class Component {
        private ItemDto rawMaterial;
        private Double quantity;

        public Component() {
        }

        public Component(ItemDto rawMaterial, Double quantity) {
            this.rawMaterial = rawMaterial;
            this.quantity = quantity;
        }
    }
}
