package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;

public class CompanyHoursMapper {

    private  CompanyHoursMapper() {}

    public static CompanyHoursResponse companyHoursToCompanyHoursResponse(CompanyHours companyHours) {
        return CompanyHoursResponse.builder()
                .closeTime(companyHours.getCloseTime())
                .openTime(companyHours.getOpenTime())
                .dayOfWeek(companyHours.getDayOfWeek())
                .open(companyHours.isOpen())
                .build();
    }


    public static CompanyHours requestToCompanyHours(CompanyHoursRequest request, Company company) {
        return new CompanyHours(
                request.getDayOfWeek(),
                request.getOpenTime(),
                request.getCloseTime(),
                request.getOpen(),
                company
        );
    }


}
