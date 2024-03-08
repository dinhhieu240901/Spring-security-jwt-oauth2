package com.codegym.controller;


import com.codegym.model.dto.UserDTO;
import com.codegym.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private IUserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userService.add(userDTO)) {
            return ResponseEntity.ok("Register success");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Register fail");
        }
    }

    @PostMapping("/checkUser/{type}")
    public ResponseEntity<?> check(@RequestBody String username, @PathVariable("type") String type) {
        if (!userService.checkUserExisted(type, username)) {
            return ResponseEntity.ok("Accept username");
        } else {
            return  ResponseEntity.status(406).body("Username already register");
        }
    }
}
