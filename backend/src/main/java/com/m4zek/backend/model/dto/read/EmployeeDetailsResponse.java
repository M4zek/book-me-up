package com.m4zek.backend.model.dto.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsResponse {

    private int  id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role_in_company;
    private String avatar;

}
