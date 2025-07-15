package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyHoursWriteModel {
    private boolean isOpen;
    private String dayOfWeek;
    private String openTime;
    private String closeTime;

    public CompanyHours toEntity(Company company) {
        return new CompanyHours(this.dayOfWeek, this.openTime, this.closeTime, this.isOpen, company);
    }
}
