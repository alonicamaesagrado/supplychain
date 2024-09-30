package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author nica
 */
public interface UserRepository extends JpaRepository<User, Integer>{
    
    
    public User findByUsercodeAndStatus(String usercode, StatusEnum status);
}
