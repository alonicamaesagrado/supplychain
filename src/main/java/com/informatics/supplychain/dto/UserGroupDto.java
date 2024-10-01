package com.informatics.supplychain.dto;

import com.informatics.supplychain.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import com.informatics.supplychain.model.UserGroup;


@Getter
@Setter

public class UserGroupDto {
    public Integer id;
    public String code;
    public Boolean isAdmin;
    public Boolean isCreator;
    public Boolean isEditor;
    public StatusEnum status;
    
    public UserGroupDto() {
        
    }
    
    public UserGroupDto(UserGroup userGroup) {
        if (userGroup != null) {
            id = userGroup.getId();
            code = userGroup.getCode();
            isAdmin = userGroup.getIsAdmin();
            isCreator = userGroup.getIsCreator();
            isEditor = userGroup.getIsEditor();
            status = userGroup.getStatus();
        }
    }
}
