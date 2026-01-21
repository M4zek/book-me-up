package com.m4zek.backend.model.dto.write;


import com.m4zek.backend.model.UserCompanyRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyRoleRequest {

    @NotNull(message = "New role is required")
    private UserCompanyRole role;

}
