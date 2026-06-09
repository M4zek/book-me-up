package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.projection.ReviewStatisticsProjection;

import java.util.List;

public class CompanyMapper {

    private CompanyMapper(){}

    public static CompanySummaryResponse companyToCompanySummaryResponse(Company company, ReviewStatisticsProjection reviewStatisticsProjection){
        return CompanySummaryResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(ImageMapper.byteImageToBase64(company.getLogo()))
                .address(AddressMapper.addressToAddressResponse(company.getAddress()))
                .rating(reviewStatisticsProjection.getAverageRating() != null ? reviewStatisticsProjection.getAverageRating() : 0)
                .numberOfReviews(Integer.parseInt(reviewStatisticsProjection.getTotalReviews().toString()))
                .build();
    }

    public static CompanyDetailsResponse companyToCompanyDetailsResponse(Company company, ReviewStatisticsResponse reviewStatisticsResponse){
        return CompanyDetailsResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .logo(ImageMapper.byteImageToBase64(company.getLogo()))
                .address(AddressMapper.addressToAddressResponse(company.getAddress()))
                .companyHours(company.getCompanyHoursList().stream()
                        .map(CompanyHoursMapper::companyHoursToCompanyHoursResponse)
                        .toList())
                .reviewStatistics(reviewStatisticsResponse)
                .owner(company.getUsers().stream()
                        .filter(item -> item.getRole().getName().equals("COMPANY_OWNER"))
                        .findFirst()
                        .map(item -> UserMapper.toEmployeeSummaryResponse(item.getUser()))
                        .orElse(null))
                .employees(company.getUsers().stream()
                        .filter(user -> !user.getRole().getName().equals("COMPANY_OWNER"))
                        .map(userRole ->  UserMapper.toEmployeeSummaryResponse(userRole.getUser()))
                        .toList().stream().toList())
                .build();
    }


    public static UserCompanyResponse companyToUserCompanyResponse(Company company, List<String> roles) {
        return UserCompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(ImageMapper.byteImageToBase64(company.getLogo()))
                .role(roles)
                .build();
    }
}
