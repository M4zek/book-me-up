package com.m4zek.backend.mapper;

import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import org.springframework.stereotype.Component;

@Component
public class CompanyHoursMapper {

    private CompanyHoursMapper() {}

    public CompanyHoursResponse companyHoursToCompanyHoursResponse(CompanyHours companyHours) {
        return CompanyHoursResponse.builder()
                .closeTime(companyHours.getCloseTime())
                .openTime(companyHours.getOpenTime())
                .dayOfWeek(companyHours.getDayOfWeek())
                .open(companyHours.isOpen())
                .build();
    }

}
