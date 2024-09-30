package service;

import enums.StatusEnum;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import repository.UserRepository;

/**
 *
 * @author nica
 */
public class UserService {
    
    @Autowired
    UserRepository userrepository;
    
    public User findByUserCodeAndStatus(String userCode, StatusEnum status) {
        User user = userrepository.findByUserCodeAndStatus(userCode, status);
        if (user == null) {
            return null;
        }
        return user;
    }
}
