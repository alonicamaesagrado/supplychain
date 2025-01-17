package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.UserGroupDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.UserGroup;
import com.informatics.supplychain.service.UserGroupService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class UserGroupController {
    @Autowired
    UserGroupService userGroupService;
    
    @GetMapping("v1/usergroup")
    ResponseEntity<UserGroupDto> getUserGroup(@RequestParam String code) {
        var userGroup = userGroupService.findByCodeAndStatus(code, StatusEnum.ACTIVE);
        return ResponseEntity.ok(new UserGroupDto(userGroup));
    }
    
    @PostMapping("v1/usergroup")
    ResponseEntity<UserGroupDto> saveUserGroup(@RequestBody UserGroupDto userGroupDto) {
        var userGroup = new UserGroup();
        userGroup.setCode(userGroupDto.getCode());
        userGroup.setIsAdmin(userGroupDto.getIsAdmin());
        userGroup.setIsCreator(userGroupDto.getIsCreator());
        userGroup.setIsEditor(userGroupDto.getIsEditor());
        return ResponseEntity.ok(new UserGroupDto(userGroupService.save(userGroup)));
    }
    
    @GetMapping("v1/usergroupList")
    ResponseEntity<List<UserGroupDto>> getUserGroupList() {
        return ResponseEntity.ok(userGroupService.findAll().stream().map(e -> new UserGroupDto(e)).collect(Collectors.toList()));
    }
    
    @PutMapping("v1/usergroup")
    public ResponseEntity<?> updateUserGroup(@RequestBody UserGroupDto userGroupDto) throws Exception {
        String userGroupCode = userGroupDto.getCode();
        var existingUsergroup = userGroupService.findByCodeAndStatus(userGroupDto.getCode(), StatusEnum.ACTIVE);
        if (existingUsergroup == null) {
            return ResponseEntity.status(404).body("User group not found.");
        }
        existingUsergroup.setCode(userGroupDto.getCode());
        existingUsergroup.setIsAdmin(userGroupDto.getIsAdmin());
        existingUsergroup.setIsCreator(userGroupDto.getIsCreator());
        existingUsergroup.setIsEditor(userGroupDto.getIsEditor());
        existingUsergroup.setStatus(userGroupDto.getStatus());
        
        var updatedUsergoup = userGroupService.save(existingUsergroup);
        return ResponseEntity.ok(new UserGroupDto(updatedUsergoup));
    }
}
