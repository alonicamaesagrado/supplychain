package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.SupplierDto;
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
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    @NotBlank(message = "Value required for code.")
    private String code;
    @NotBlank(message = "Value required for name.")
    private String name;
    private String address;
    private String company;
    private String terms;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public Supplier() {
        status = StatusEnum.ACTIVE;
    }
 
    public Supplier(SupplierDto dto) {
        code = dto.getCode();
        name = dto.getName();
    }
}
