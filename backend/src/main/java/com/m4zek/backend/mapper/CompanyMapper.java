package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.Review;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.read.ReviewResponse;

import java.util.stream.Collectors;

public class CompanyMapper {

    private CompanyMapper(){}

    public static CompanySummaryResponse companyToCompanySummaryResponse(Company company){
        return CompanySummaryResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(company.getLogo())
                .address(AddressMapper.addressToAddressResponse(company.getAddress()))
                .rating(
                        company.getCompanyOffers().stream()
                                .flatMap(offer -> offer.getReviews().stream())
                                .map(Review::toReadModel)
                                .mapToInt(ReviewResponse::getRating)
                                .average()
                                .orElse(0.0)
                )
                .numberOfReviews(company.getCompanyOffers()
                        .stream()
                        .mapToInt(offer -> offer.getReviews().size())
                        .sum())
                .build();
    }

    public static CompanyDetailsResponse companyToCompanyDetailsResponse(Company company){
        return CompanyDetailsResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .logo(company.getLogo())
                .address(AddressMapper.addressToAddressResponse(company.getAddress()))
                .companyHours(company.getCompanyHoursList().stream()
                        .map(CompanyHoursMapper::companyHoursToCompanyHoursResponse)
                        .toList())
                .reviewStatistics(company.getCompanyReviewStatistics())
                .owner(company.getUsers().stream()
                        .filter(item -> item.getRole().getName().equals("COMPANY_OWNER"))
                        .findFirst()
                        .map(item -> UserMapper.toEmployeeSummaryResponse(item.getUsers()))
                        .orElse(null))
                .employees(company.getUsers().stream()
                        .filter(user -> !user.getRole().getName().equals("COMPANY_OWNER"))
                        .findAny()
                        .map(userRole ->  UserMapper.toEmployeeSummaryResponse(userRole.getUsers()))
                        .stream().collect(Collectors.toList()))
                .build();
    }


}
