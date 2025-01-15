package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginDto {

    public String usercode;
    public String first_name;
    public String last_name;
    public String email;
    public UserGroupDto userGroup;
    public StatusEnum status;

    public LoginDto() {

    }

    public LoginDto (User entity) {
        if (entity != null) {
            usercode = entity.getUsercode();
            first_name = entity.getFirst_name();
            last_name = entity.getLast_name();
            email = entity.getEmail();
            if (entity.getUserGroup() != null) {
                userGroup = new UserGroupDto(entity.getUserGroup());
            }
            status = entity.getStatus();
        }
    }
}
