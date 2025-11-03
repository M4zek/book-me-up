package com.m4zek.backend.mapper;

import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;

public class CompanyHoursMapper {

    private  CompanyHoursMapper() {}

    public static CompanyHoursResponse companyHoursToCompanyHoursResponse(CompanyHours companyHours) {
        return CompanyHoursResponse.builder()
                .closeTime(companyHours.getCloseTime())
                .openTime(companyHours.getOpenTime())
                .dayOfWeek(companyHours.getDayOfWeek())
                .isOpen(companyHours.isOpen())
                .build();
    }

}
