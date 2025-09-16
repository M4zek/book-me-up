package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
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
public class CompanyHoursWriteModel {

    @NotNull(message = "You must specify whether the company is open")
    private Boolean isOpen;

    @NotBlank(message = "Day of week is required")
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

    public CompanyHours toEntity(Company company) {
        return new CompanyHours(this.dayOfWeek, this.openTime, this.closeTime, this.isOpen, company);
    }
}
