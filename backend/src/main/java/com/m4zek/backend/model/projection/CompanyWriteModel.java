package com.m4zek.backend.model.projection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyWriteModel {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1024, message = "Description must not exceed 1024 characters")
    private String description;

    @Pattern(
            regexp = "^[A-Za-z0-9+/=]+$",
            message = "Logo must be a valid Base64 string"
    )
    private String logo;

    @Valid
    private AddressWriteModel address;

    @Valid
    private CategoryWriteModel category;

}
