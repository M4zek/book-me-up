package com.m4zek.backend.controller.admin;

import com.m4zek.backend.model.dto.admin.DashboardSummaryResponse;
import com.m4zek.backend.model.dto.admin.UserStatsResponse;
import com.m4zek.backend.service.facade.admin.AdminStatisticFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin/stats")
public class AdminStatisticController {

    private final AdminStatisticFacade adminStatisticFacade;

    public AdminStatisticController(AdminStatisticFacade adminStatisticFacade) {
        this.adminStatisticFacade = adminStatisticFacade;
    }


    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardStats(){
        DashboardSummaryResponse response = this.adminStatisticFacade.getSystemStats();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_VIEWER')")
    public ResponseEntity<UserStatsResponse> getUsersStats(){
        UserStatsResponse response = this.adminStatisticFacade.getUsersStats();
        return ResponseEntity.ok(response);
    }
}
