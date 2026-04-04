package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@ToString
public class CompanyHoursRequest {

    @NotNull(message = "You must specify whether the company is open")
    private Boolean isOpen;

    @NotBlank(message = "Day of week is required")
    @Pattern(
            regexp = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday",
            message = "Invalid day of week"
    )
    private String dayOfWeek;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "Open time must be in format HH:mm"
    )
    private String openTime;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "Close time must be in format HH:mm"
    )
    private String closeTime;

}
