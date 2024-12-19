package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.UserDto;
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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    @NotBlank(message = "Value required for code.")
    private String usercode;
    @NotBlank(message = "Value required for password.")
    private String password;
    @NotBlank(message = "Value required for first name.")
    private String first_name;
    @NotBlank(message = "Value required for last name.")
    private String last_name;
    
    private String email;
    
    @JoinColumn
    @ManyToOne
    private UserGroup userGroup;
    
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    
    public User() {
        status = StatusEnum.ACTIVE;
    }
 
    public User(UserDto dto) {
        usercode = dto.getUsercode();
        password = dto.getPassword();
        first_name = dto.getFirst_name();
        last_name = dto.getLast_name();
        email = dto.getEmail();
        if (dto.getUserGroup()!= null) {
            userGroup = new UserGroup(dto.getUserGroup());
        }
    }
}
