package com.m4zek.backend.model.dto.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetailsResponse {

    private int id;
    private String name;
    private String description;
    private AddressResponse address;
    private List<CompanyHoursResponse> companyHours;
    private ReviewStatisticsResponse reviewStatistics;
    private EmployeeSummaryResponse owner;
    private List<EmployeeSummaryResponse> employees;
    private String logo_url;

}
