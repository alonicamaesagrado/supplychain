package com.informatics.supplychain.controller;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.service.UserService;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    UserService userService;

    protected boolean verify(String usercode, String token) {
        try {
            String password = userService.findByUserCodeAndStatus(usercode, StatusEnum.ACTIVE).getPassword();
            byte[] bytesOfMessage = password.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(bytesOfMessage);
            StringBuilder sb = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                sb.append("0123456789abcdef".charAt((b & 0xF0) >> 4));
                sb.append("0123456789abcdef".charAt((b & 0x0F)));
            }
            String hex = sb.toString();
            if (token.equals(hex)) {
                return true;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }
}
