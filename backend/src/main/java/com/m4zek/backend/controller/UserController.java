package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.read.UserToHiredResponse;
import com.m4zek.backend.model.projection.MemberProjection;
import com.m4zek.backend.service.facade.UserFacade;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserFacade userFacade;
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getLoggedInUser() {
        UserResponse response = this.userFacade.getLoggedUserDetails();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users/hire/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<UserToHiredResponse>> findUsersToHire(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName
    ){
        Page<UserToHiredResponse> response = this.userFacade.searchUsersToHireIntoCompany(pageable, firstName, lastName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<MemberProjection>> findUsers(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "First name cannot be empty") String firstName,
            @RequestParam(required = false) @Size(min = 1, message = "Last name cannot be empty") String lastName
    ){
        Page<MemberProjection> response = this.userFacade.searchUsers(pageable, firstName, lastName);
        return ResponseEntity.ok(response);
    }
}
