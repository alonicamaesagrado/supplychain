package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.UserDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.User;
import com.informatics.supplychain.service.UserGroupService;
import com.informatics.supplychain.service.UserService;
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
    ResponseEntity<UserDto> getUser(@RequestParam String usercode) {
        var user = userService.findByUserCodeAndStatus(usercode, StatusEnum.ACTIVE);
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
        if (userGroup == null) {
            throw new Exception("User Group does not exist!");
        }
        user.setUserGroup(userGroup);
        return ResponseEntity.ok(new UserDto(userService.save(user)));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String usercode = loginData.get("username");
        String password = loginData.get("password");

        boolean isAuthenticated = userService.authenticate(usercode, password);
        if (isAuthenticated) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
