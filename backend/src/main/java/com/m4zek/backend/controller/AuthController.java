package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.AuthResponse;
import com.m4zek.backend.model.dto.read.RefreshTokenResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.write.LoginRequest;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.service.facade.AuthFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createAccount(@Valid @RequestBody UserRequest user) {
        UserResponse response = authFacade.createAccount(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = this.authFacade.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("refreshToken/{token}")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid
            @Size(min = 36, max = 36, message = "Refresh token must be exactly 36 characters long")
            @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Refresh token can only contain letters, numbers, and hyphens")
            @PathVariable String token){
        RefreshTokenResponse response = this.authFacade.refreshToken(token);
        return ResponseEntity.ok(response);
    }

}
