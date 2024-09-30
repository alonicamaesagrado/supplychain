package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.User;

/**
 *
 * @author nica
 */

@Getter
@Setter

public class UserDto {
    public String usercode;
    public String password;
    public String first_name;
    public String last_name;
    public StatusEnum status;
    
    public UserDto() {
        
    }
    
    public UserDto(User user) {
        if (user != null) {
            usercode = user.getUsercode();
            password = user.getPassword();
            first_name = user.getFirst_name();
            last_name = user.getLast_name();
            status = user.getStatus();
        }
    }
}
