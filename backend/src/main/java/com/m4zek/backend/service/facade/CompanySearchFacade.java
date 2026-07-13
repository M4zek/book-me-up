package com.m4zek.backend.service.facade;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.CompanySummaryResponse;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.service.CompanySearchService;
import com.m4zek.backend.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanySearchFacade {

    private final CompanyMapper companyMapper;
    private final ReviewMapper reviewMapper;

    private final CompanySearchService companySearchService;
    private final ReviewService reviewService;

    public CompanySearchFacade(CompanyMapper companyMapper, ReviewMapper reviewMapper, CompanySearchService companySearchService, ReviewService reviewService) {
        this.companyMapper = companyMapper;
        this.reviewMapper = reviewMapper;
        this.companySearchService = companySearchService;
        this.reviewService = reviewService;
    }

    // Search recommended Companies by city, category
    public Page<CompanySummaryResponse> searchRecommendedCompanies(Pageable pageable, String city, String category){
        // Find companies based on category, city and sorted by average rating
        Page<Company> recommendedCompanies = this.companySearchService.searchRecommendedCompanies(city, category, pageable);

        // Mapping company into CompanySummaryResponse
        List<CompanySummaryResponse> mappedCompanies =  recommendedCompanies.stream()
                .map(company -> {
                    var reviewStatistics = this.reviewService.findCompanyReviewsStats(company.getId());
                    return this.companyMapper.companyToCompanySummaryResponse(company, reviewStatistics);
                }).toList();

        return new PageImpl<>(mappedCompanies, pageable, recommendedCompanies.getTotalElements());
    }

    // Search companies based on name, category or city.
    public Page<CompanySummaryResponse> searchCompanies(Pageable pageable, String companyName, String city, String category){
        // Find companies based on category, city, company name.
        Page<Company> searchCompaniesResult = this.companySearchService.searchCompanies(
                pageable, companyName, city, category
        );

        // Mapping company into CompanySummaryResponse
        List<CompanySummaryResponse> mappedCompanies = searchCompaniesResult.stream()
                .map(company -> {
                    var reviewStatistics = this.reviewService.findCompanyReviewsStats(company.getId());
                    return this.companyMapper.companyToCompanySummaryResponse(company, reviewStatistics);
                }).toList();

        return new PageImpl<>(mappedCompanies, pageable, searchCompaniesResult.getTotalElements());
    }


    // Search company based on company id
    public CompanyDetailsResponse findCompany(int companyId){
        // Find company by id
        Company company = this.companySearchService.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // Create review statistics based on all company opinions
        List<Object[]> reviewStatistics = this.reviewService.findCompanyReviewsData(company.getId());
        ReviewStatisticsResponse statsResponse = this.reviewMapper.reviewsToReviewsResponse(reviewStatistics);

        // Return mapped company into CompanyDetailsResponse
        return this.companyMapper.companyToCompanyDetailsResponse(company, statsResponse);
    }
}
