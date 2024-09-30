package dto;

import model.User;

/**
 *
 * @author nica
 */
public class UserDto {
    public String userCode;
    public String password;
    
    public UserDto() {
        
    }
    
    public UserDto(User entity) {
        if (entity != null) {
            userCode = entity.getUsercode();
            password = entity.getPassword();
        }
    }
}
