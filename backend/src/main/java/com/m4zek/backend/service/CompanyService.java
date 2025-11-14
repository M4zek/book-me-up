package com.m4zek.backend.service;

import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public CompanyService(CompanyRepository companyRepository,
                          CategoryRepository categoryRepository,
                          CompanyRoleRepository companyRoleRepository,
                          UserRepository userRepository, ReviewRepository reviewRepository) {
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
        this.companyRoleRepository = companyRoleRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public CompanyDetailsResponse saveCompany(CompanyRequest companyRequest) {
        User owner = userRepository.findById(companyRequest.getOwner_id())
                .orElseThrow(() -> new UserNotFoundException("User with given id not found"));

        Category category = getCategory(companyRequest.getCategory().getName());

        Address address = companyRequest.getAddress().toEntity();

        byte[] logoBytes = companyRequest.getLogo() != null ?
                Base64.getDecoder().decode(companyRequest.getLogo()) : null;

        Company company = new Company(
                companyRequest.getName(),
                companyRequest.getDescription(),
                logoBytes,
                category,
                address
        );

        address.assignCompany(company);

        CompanyRole companyRole = companyRoleRepository.findByName("COMPANY_OWNER")
                .orElseThrow(() -> new CategoryNotFoundException("Category with name COMPANY_OWNER not found"));

        CompanyUserRole companyUserRole = new CompanyUserRole(owner, company, companyRole);

        company.addUserRole(companyUserRole);

        Company savedCompany = companyRepository.save(company);

        return CompanyMapper.companyToCompanyDetailsResponse(savedCompany, new ReviewStatisticsResponse());
    }


    public Page<CompanySummaryResponse> readRecommendedCompany(Pageable pageable, String city, String category) {
        Page<Company> recommendedCompaniesPage = this.companyRepository
                .findAllOrderByAverageRatingDesc(pageable, city, category);

        List<CompanySummaryResponse> companySummaryResponse = recommendedCompaniesPage.stream().map(
                company -> {
                    var reviewStatisticsProjection = this.reviewRepository.findReviewStatsByCompanyId(company.getId());
                    return CompanyMapper.companyToCompanySummaryResponse(company, reviewStatisticsProjection);
                }
        ).toList();
        return new PageImpl<>(companySummaryResponse, pageable, recommendedCompaniesPage.getTotalElements());
    }

    public CompanyDetailsResponse readCompanyDetails(int companyId) {
        Company company = this.getCompany(companyId);
        List<Object[]> reviews = this.reviewRepository.findAllByCompanyId(companyId);
        ReviewStatisticsResponse reviewStatisticsResponse = ReviewMapper.reviewsToReviewsResponse(reviews);
        return CompanyMapper.companyToCompanyDetailsResponse(company, reviewStatisticsResponse);
    }

    public Page<CompanySummaryResponse> searchCompaniesByNameCityCategory(Pageable pageable, String companyName, String city, String categoryName) {

        Page<Company> resultPage = this.companyRepository.searchCompanyByNameAndCityAndCategoryName(
                pageable, companyName, city, categoryName);

        List<CompanySummaryResponse> resultList = resultPage.stream().map(
                company -> {
                    var reviewStatisticsProjection = this.reviewRepository.findReviewStatsByCompanyId(company.getId());
                    return CompanyMapper.companyToCompanySummaryResponse(company, reviewStatisticsProjection);
                }).toList();
        return new PageImpl<>(resultList, pageable, resultPage.getTotalElements());
    }


    /* ************************************
                 PRIVATE METHODS
    *************************************** */

    private Company getCompany(int companyId) {
        return this.companyRepository.findById(companyId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }

    private Category getCategory(String categoryName) {
        return this.categoryRepository.findByName(categoryName).orElseThrow(
                () -> new CategoryNotFoundException("Category with given name not found")
        );
    }

}
