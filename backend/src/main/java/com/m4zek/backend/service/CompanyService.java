package com.m4zek.backend.service;

import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.*;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.model.dto.write.UserCompanyRoleRequest;
import com.m4zek.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CompanyUserRoleRepository companyUserRoleRepository;

    public CompanyService(CompanyRepository companyRepository,
                          CategoryRepository categoryRepository,
                          CompanyRoleRepository companyRoleRepository,
                          UserRepository userRepository, ReviewRepository reviewRepository, CompanyUserRoleRepository companyUserRoleRepository) {
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
        this.companyRoleRepository = companyRoleRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.companyUserRoleRepository = companyUserRoleRepository;
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

    public List<EmployeeSummaryResponse> findEmployeesByCompanyId(int companyId) {
        Company company = this.getCompany(companyId);
        return company.getUsers().stream()
                .map(
                        item -> UserMapper.toEmployeeSummaryResponse(item.getUser())
                ).toList();
    }

    public Page<EmployeeDetailsResponse> findEmployeesDetailsByCompanyId(int companyId, Pageable pageable) {
        Page<CompanyUserRole> companyUserRoles = this.companyUserRoleRepository.findAllByCompanyId(companyId, pageable);
        List<EmployeeDetailsResponse> responseList = companyUserRoles.stream()
                .map(userRole ->
                        UserMapper.toEmployeeDetailsResponse(
                            userRole.getUser(),
                            userRole.getRole().getName())
                )
                .toList();
        return new PageImpl<>(responseList, pageable, companyUserRoles.getTotalElements());
    }

    // Method to change user role in company based on companyId, employeeId(USER ID) and new role from body
    public EmployeeDetailsResponse updateEmployeeRoleInCompany(int companyId, int employeeId, UserCompanyRoleRequest body) {
        CompanyUserRole companyUserRoleRelation = this.companyUserRoleRepository.findByUserIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Employee with id " + employeeId + " not found in company with id " + companyId));


        String oldRoleTxt = companyUserRoleRelation.getRole().getName();
        String newRoleTxt = body.getRole().toString();

        CompanyRole newRole = this.companyRoleRepository.findByName(newRoleTxt)
                .orElseThrow(() -> new CompanyNotFoundException("Role with name " + newRoleTxt + " not found"));

        companyUserRoleRelation.assignRole(newRole);
        this.companyUserRoleRepository.save(companyUserRoleRelation);


        logger.info("Employee[{}] in Company[{}] has change role from [{}] to [{}]", employeeId, companyId, oldRoleTxt, newRoleTxt);

        return UserMapper.toEmployeeDetailsResponse(
                companyUserRoleRelation.getUser(),
                companyUserRoleRelation.getRole().getName()
        );
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
