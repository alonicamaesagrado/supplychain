package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import com.informatics.supplychain.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User findByUserCodeAndStatus(String usercode, StatusEnum status) {
        return userRepository.findByUsercodeAndStatus(usercode, status);
    }
    
    public List<User> findAll(){
      return userRepository.findAll();
    }
    
    public User save(User user){
       return userRepository.save(user);
    }
    
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}
