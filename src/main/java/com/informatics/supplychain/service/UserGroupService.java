package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.UserGroup;
import com.informatics.supplychain.repository.UserGroupRepository;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class UserGroupService {

    @Autowired
    UserGroupRepository userGroupRepository;

    public UserGroup findByCodeAndStatus(String code, StatusEnum status) {
        return userGroupRepository.findByCodeAndStatus(code, status);
    }
    
    public List<UserGroup> findByStatus(StatusEnum status) {
        return userGroupRepository.findByStatus(status);
    }
    
    public UserGroup findById(Integer id){
      return userGroupRepository.findById(id).orElse(null);
    }
    
    public UserGroup findByIdAndStatus(Integer id, StatusEnum status) {
        return userGroupRepository.findByIdAndStatus(id, status);
    }
    
    public List<UserGroup> findAll(){
      return userGroupRepository.findAll();
    }
    
    public UserGroup save(UserGroup userGroup){
       return userGroupRepository.save(userGroup);
    }
}
