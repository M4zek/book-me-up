package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyProfileRequest {

//    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

}
