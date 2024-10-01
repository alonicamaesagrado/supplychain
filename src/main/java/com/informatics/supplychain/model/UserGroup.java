package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.UserGroupDto;
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
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    @NotBlank(message = "Value required for code.")
    private String code;
    private Boolean isAdmin;
    private Boolean isCreator;
    private Boolean isEditor;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public UserGroup() {
        status = StatusEnum.ACTIVE;
    }
 
    public UserGroup(UserGroupDto dto) {
        code = dto.getCode();
        isAdmin = dto.getIsAdmin();
        isCreator = dto.getIsCreator();
        isEditor = dto.getIsEditor();
    }
}
