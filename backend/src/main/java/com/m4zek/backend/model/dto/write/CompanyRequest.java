package com.m4zek.backend.model.dto.write;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompanyRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1024, message = "Description must not exceed 1024 characters")
    private String description;

    @Positive
    private int owner_id;

    @Valid
    private AddressRequest address;

    @Valid
    private CategoryRequest category;

    @Valid
    private List<CompanyHoursRequest> openingHours;

}
