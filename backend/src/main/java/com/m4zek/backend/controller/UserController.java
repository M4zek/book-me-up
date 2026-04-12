package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.read.UserToHiredResponse;
import com.m4zek.backend.model.projection.MemberProjection;
import com.m4zek.backend.service.UserCompanyService;
import com.m4zek.backend.service.UserService;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/users/hire/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<UserToHiredResponse>> findUsersToHire(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName
    ){
        return ResponseEntity.ok(this.userCompanyService.findUsersToHireByFirstNameAndSurname(pageable, firstName, lastName));
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<MemberProjection>> findUsers(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName
    ){
        return ResponseEntity.ok(this.userCompanyService.findUsersToChat(pageable, firstName, lastName));
    }
}
