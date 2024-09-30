package controller;

import dto.UserDto;
import enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

/**
 *
 * @author nica
 */
//public class UserController extends BaseController {
public class UserController {
    @Autowired
    private UserService userService;
     
    @GetMapping("/v1/user")
    private ResponseEntity<UserDto> getUsers(@RequestParam String userCode) {       
        return ResponseEntity.ok(new UserDto(userService.findByUserCodeAndStatus(userCode, StatusEnum.ACTIVE)));
    }
}
