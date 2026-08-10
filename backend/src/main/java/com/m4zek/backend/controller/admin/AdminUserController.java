package com.m4zek.backend.controller.admin;


import com.m4zek.backend.model.RoleEnum;
import com.m4zek.backend.model.UserStatus;
import com.m4zek.backend.model.dto.admin.AccountListItemResponse;
import com.m4zek.backend.model.dto.admin.AccountSummaryResponse;
import com.m4zek.backend.model.projection.MonthCountProjection;
import com.m4zek.backend.service.facade.admin.AdminUserFacade;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/admin")
public class AdminUserController {


    private final AdminUserFacade userFacade;


    public AdminUserController(AdminUserFacade userFacade) {
        this.userFacade = userFacade;
    }


    @GetMapping("/users/growth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<List<MonthCountProjection>> getUsersGrowth(){
        List<MonthCountProjection> response = this.userFacade.readUserGrowthLastYear();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users/login-trend")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<List<MonthCountProjection>> getUserLoginTrend(){
        List<MonthCountProjection> response = this.userFacade.readUserLoginTrend();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users/new-accounts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<Page<AccountSummaryResponse>> findLastCreatedAccount(Pageable pageable){
        Page<AccountSummaryResponse> lastAccounts = this.userFacade.readLastCreatedAccount(pageable);
        return ResponseEntity.ok(lastAccounts);
    }

    @GetMapping("/users/last-loggedin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<Page<AccountSummaryResponse>> findLastLoggedIn(Pageable pageable){
        Page<AccountSummaryResponse> lastAccountLoggedIn = this.userFacade.readLastAccountLoggedIn(pageable);
        return ResponseEntity.ok(lastAccountLoggedIn);
    }


    @GetMapping("/users/last-admin-loggedin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<Page<AccountSummaryResponse>> findAdminHistoryLogin(Pageable pageable){
        Page<AccountSummaryResponse> lastAdminHistoryLogin = this.userFacade.findLastActiveHistoryAdmins(pageable);
        return ResponseEntity.ok(lastAdminHistoryLogin);
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<Page<AccountListItemResponse>> findAccounts(
            Pageable pageable,
            @RequestParam(required = false)
            @Size(min = 2, max = 60, message = "Query should contains between 2 and 60 chars")
            String query,

            @RequestParam(required = false) List<UserStatus> statuses,
            @RequestParam(required = false) List<RoleEnum> roles
    )
    {
        Page<AccountListItemResponse> accounts = this.userFacade.findAccounts(pageable, query, statuses, roles);

        return ResponseEntity.ok(accounts);
    }



}
