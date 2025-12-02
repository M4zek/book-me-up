package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.service.UserCompanyService;
import com.m4zek.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final UserCompanyService userCompanyService;

    public UserController(UserService userService, UserCompanyService userCompanyService) {
        this.userService = userService;
        this.userCompanyService = userCompanyService;
    }

    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getLoggedInUser() {
        return ResponseEntity.ok(this.userService.findLoggedInUser());
    }

    @GetMapping("/users/me/companies")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<UserCompanyResponse>> findAllUserCompany(){
        return ResponseEntity.ok(this.userCompanyService.findAllUserCompanies());
    }

}
