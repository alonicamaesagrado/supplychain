package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.LoginDto;
import com.informatics.supplychain.dto.UserDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import com.informatics.supplychain.service.UserGroupService;
import com.informatics.supplychain.service.UserService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserGroupService userGroupService;

    @GetMapping("v1/user")
    ResponseEntity<?> getUser(@RequestParam String usercode) {
        var user = userService.findByUserCodeAndStatus(usercode, StatusEnum.ACTIVE);
        
        if (user == null) {
            return ResponseEntity.status(404).body("User not found.");
        }
        return ResponseEntity.ok(new UserDto(user));
    }

    @GetMapping("v1/userList")
    ResponseEntity<List<UserDto>> getUserList() {
        return ResponseEntity.ok(userService.findAll().stream().map(e -> new UserDto(e)).collect(Collectors.toList()));
    }

    @PostMapping("v1/user")
    ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) throws Exception {
        var user = new User();
        var userGroup = userGroupService.findById(userDto.getUserGroup().getId());
        user.setUsercode(userDto.getUsercode());
        user.setPassword(userDto.getPassword());
        user.setFirst_name(userDto.getFirst_name());
        user.setLast_name(userDto.getLast_name());
        user.setEmail(userDto.getEmail());
        if (userGroup == null) {
            throw new Exception("User Group does not exist!");
        }
        user.setUserGroup(userGroup);
        return ResponseEntity.ok(new UserDto(userService.save(user)));
    }

    @PostMapping("v1/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        User user = userService.findByUserCodeAndStatus(userDto.getUsercode(), StatusEnum.ACTIVE);
        if (user != null && user.getPassword().equals(userDto.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            var responseDto = new LoginDto (user);
            
            response.put("userDetails:  ", responseDto);
            response.put("loggedInAt:   ", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
