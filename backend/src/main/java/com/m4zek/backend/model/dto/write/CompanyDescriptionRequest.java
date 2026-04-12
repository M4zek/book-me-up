package com.m4zek.backend.model.dto.write;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDescriptionRequest {

    @NotBlank(message = "Description is required")
    @Size(max = 1024, message = "Description must not exceed 1024 characters")
    private String description;

}
