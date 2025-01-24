package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    
    public User findByUsercodeAndStatus(String usercode, StatusEnum status);
    
    List<User> findByStatus(StatusEnum status);
    
    boolean existsByUsercode(String usercode);

    boolean existsByEmail(String email);
}
