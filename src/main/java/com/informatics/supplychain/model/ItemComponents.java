package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.enums.StatusEnum;
import jakarta.persistence.*;
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

    private Double quantity;
    
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public ItemComponents() {
        this.status = StatusEnum.ACTIVE;
    }

    public ItemComponents(ItemDto finishProductDto, ItemDto rawMaterialDto, Double quantity) {
        if (finishProductDto != null) {
            this.finishProduct = new Item(finishProductDto);
        }
        if (rawMaterialDto != null) {
            this.rawMaterial = new Item(rawMaterialDto);
        }
        this.quantity = quantity;
        this.status = StatusEnum.ACTIVE;
    }
}
