package com.m4zek.backend.controller;

import com.m4zek.backend.model.User;
import com.m4zek.backend.model.projection.UserWriteModel;
import com.m4zek.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    private ResponseEntity<User> createAccount(@RequestBody @Valid UserWriteModel user) {
        return ResponseEntity.ok(userService.createUser(user));
    }


}
