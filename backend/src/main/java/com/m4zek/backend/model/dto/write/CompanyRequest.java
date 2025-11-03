package com.m4zek.backend.model.dto.write;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRequest {

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

    @Positive
    private int owner_id;

    @Valid
    private AddressRequest address;

    @Valid
    private CategoryRequest category;

}
