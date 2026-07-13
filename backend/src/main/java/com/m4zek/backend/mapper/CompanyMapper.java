package com.m4zek.backend.mapper;

import com.m4zek.backend.minio.MinioUrlResolver;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.read.*;
import com.m4zek.backend.model.projection.ReviewStatisticsProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyMapper {

    private final MinioUrlResolver urlResolver;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final CompanyHoursMapper hoursMapper;

    private CompanyMapper(MinioUrlResolver urlResolver, UserMapper userMapper, AddressMapper addressMapper, CompanyHoursMapper hoursMapper){
        this.urlResolver = urlResolver;
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
        this.hoursMapper = hoursMapper;
    }

    public CompanySummaryResponse companyToCompanySummaryResponse(Company company,
                                                                         ReviewStatisticsProjection reviewStatisticsProjection){
        return CompanySummaryResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(this.urlResolver.imageUrlSmall(company.getLogoFile()))
                .address(this.addressMapper.addressToAddressResponse(company.getAddress()))
                .rating(reviewStatisticsProjection.getAverageRating() != null ? reviewStatisticsProjection.getAverageRating() : 0)
                .numberOfReviews(Integer.parseInt(reviewStatisticsProjection.getTotalReviews().toString()))
                .build();
    }


    public CompanyDetailsResponse companyToCompanyDetailsResponse(Company company,
                                                                         ReviewStatisticsResponse reviewStatisticsResponse){
        return CompanyDetailsResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .logo_url(this.urlResolver.imageUrlMedium(company.getLogoFile()))
                .address(this.addressMapper.addressToAddressResponse(company.getAddress()))
                .companyHours(company.getCompanyHoursList().stream()
                        .map(this.hoursMapper::companyHoursToCompanyHoursResponse)
                        .toList())
                .reviewStatistics(reviewStatisticsResponse)
                .owner(this.getCompanyOwner(company))
                .employees(this.getCompanyEmployees(company))
                .build();
    }


    public UserCompanyResponse companyToUserCompanyResponse(Company company, List<String> roles) {
        return UserCompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(this.urlResolver.imageUrlSmall(company.getLogoFile()))
                .role(roles)
                .build();
    }

    private List<EmployeeSummaryResponse> getCompanyEmployees(Company company){
        return company.getUsers().stream()
                .filter(user -> !user.getRole().getName().equals("COMPANY_OWNER"))
                .map(userRole -> this.userMapper.toEmployeeSummaryResponse(userRole.getUser()))
                .toList().stream().toList();
    }

    private EmployeeSummaryResponse getCompanyOwner(Company company){
        return company.getUsers().stream()
                .filter(item -> item.getRole().getName().equals("COMPANY_OWNER"))
                .findFirst()
                .map(item -> this.userMapper.toEmployeeSummaryResponse(item.getUser()))
                .orElse(null);
    }

}
