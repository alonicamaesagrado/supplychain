package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer>{
    
    
    public UserGroup findByCodeAndStatus(String code, StatusEnum status);
}
