package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    @NotBlank(message = "Value required for code.")
    private String code;
    @NotBlank(message = "Value required for description.")
    private String description;
    private String category;
    private String brand;
    private String unit;
    private Double reorderPoint;
    private Double price;
    private Double cost;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public Item() {
        status = StatusEnum.ACTIVE;
    }
 
    public Item(ItemDto dto) {
        id = dto.getId();
        code = dto.getCode();
        description = dto.getDescription();
        category = dto.getCategory();
        brand = dto.getBrand();
        unit = dto.getUnit();
        reorderPoint = dto.getReorderPoint();
        price = dto.getPrice();
        cost = dto.getCost();
    }
}
