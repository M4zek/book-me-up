package com.m4zek.backend.controller;

import com.m4zek.backend.exception.RefreshTokenException;
import com.m4zek.backend.model.RefreshToken;
import com.m4zek.backend.model.dto.read.AuthResponse;
import com.m4zek.backend.model.dto.read.RefreshTokenResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.write.LoginRequest;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.security.jwt.TokenManager;
import com.m4zek.backend.security.service.MyUserDetails;
import com.m4zek.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenManager tokenManager,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createAccount(@Valid @RequestBody UserRequest user) {
        UserResponse savedUser = userService.createNewUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenManager.generateToken(authentication);
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String refreshToken = userService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(
                new AuthResponse(
                        userDetails.getId(),
                        token,
                        refreshToken,
                        roles
                )
        );
    }


    @PostMapping("refreshToken/{token}")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid
            @Size(min = 36, max = 36, message = "Refresh token must be exactly 36 characters long")
            @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Refresh token can only contain letters, numbers, and hyphens")
            @PathVariable String token){
        String newJsonWebToken = userService.findRefreshTokenByToken(token)
                .map(userService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> tokenManager.generateToken(
                        userService.findUserById(user.getId()).getAddressEmail()
                        )
                )
                .orElseThrow(() -> new RefreshTokenException(token ,"Invalid refresh token"));

        String refreshToken = userService.findRefreshTokenByToken(token)
                .map(userService::updateExpirationTime)
                .map(RefreshToken::getRefreshToken)
                .orElseThrow(() -> new RefreshTokenException(token ,"Invalid refresh token"));

        return ResponseEntity.ok(new RefreshTokenResponse(newJsonWebToken, refreshToken));
    }

}
