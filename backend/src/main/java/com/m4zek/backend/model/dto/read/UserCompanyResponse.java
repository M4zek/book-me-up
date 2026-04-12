package com.m4zek.backend.model.dto.read;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCompanyResponse {
    private int id;
    private String name;
    private String logo;
    private List<String> role;
}
