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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    ResponseEntity<List<UserDto>> getUserList(@RequestParam(required = false) StatusEnum status) {
        List<User> user;

        if (status != null) {
            user = userService.findByStatus(status);
        } else {
            user = userService.findAll();
        }
        List<UserDto> userDtos = user.stream().map(UserDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @PostMapping("v1/user")
    ResponseEntity<?> saveUser(@RequestBody UserDto userDto) throws Exception {
        var user = new User();
        var userGroup = userGroupService.findById(userDto.getUserGroup().getId());
        
        if (userService.existsByUsercode(userDto.getUsercode())) {
            return ResponseEntity.status(400).body("Usercode already exists.");
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(400).body("Email already exists.");
        }
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

    @PutMapping("v1/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, @RequestBody UserDto userDto) throws Exception {
        var existingUser = userService.findById(id);
        var userGroup = userGroupService.findById(userDto.getUserGroup().getId());
        
        //validations
        if (existingUser == null) {
            return ResponseEntity.status(404).body("User not found.");
        }
        if (!existingUser.getUsercode().equals(userDto.getUsercode()) && userService.existsByUsercode(userDto.getUsercode())) {
            return ResponseEntity.status(400).body("Usercode already exists.");
        }
        if (!existingUser.getEmail().equals(userDto.getEmail()) && userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(400).body("Email already exists.");
        }
        if (userGroup == null) {
            throw new Exception("User Group does not exist!");
        }
        
        existingUser.setUsercode(userDto.getUsercode());
        existingUser.setPassword(userDto.getPassword());
        existingUser.setFirst_name(userDto.getFirst_name());
        existingUser.setLast_name(userDto.getLast_name());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setUserGroup(userGroup);
        existingUser.setStatus(userDto.getStatus());
        var updatedUser = userService.save(existingUser);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    @PostMapping("v1/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        User user = userService.findByUserCodeAndStatus(userDto.getUsercode(), StatusEnum.ACTIVE);
        if (user != null && user.getPassword().equals(userDto.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            var responseDto = new LoginDto(user);

            response.put("userDetails:  ", responseDto);
            response.put("loggedInAt:   ", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
