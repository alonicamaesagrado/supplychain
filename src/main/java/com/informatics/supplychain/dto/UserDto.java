package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.User;

@Getter
@Setter

public class UserDto {

    public String usercode;
    public String password;
    public String first_name;
    public String last_name;
    public String email;
    public UserGroupDto userGroup;
    public StatusEnum status;

    public UserDto() {

    }

    public UserDto(User entity) {
        if (entity != null) {
            usercode = entity.getUsercode();
            password = entity.getPassword();
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
