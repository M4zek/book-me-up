package com.m4zek.backend.service;

import com.m4zek.backend.exception.*;
import com.m4zek.backend.mapper.*;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.*;
import com.m4zek.backend.model.dto.write.*;
import com.m4zek.backend.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Transactional
    public CompanyDetailsResponse saveCompany(CompanyRequest companyRequest) {
        User owner = userRepository.findById(companyRequest.getOwner_id())
                .orElseThrow(() -> new UserNotFoundException("User with given id not found"));

        Category category = getCategory(companyRequest.getCategory().getName());

        this.validateNoDuplicateDays(companyRequest.getOpeningHours());

        Address address = new Address(
                companyRequest.getAddress().getCity(),
                companyRequest.getAddress().getPostalCode(),
                companyRequest.getAddress().getStreet(),
                companyRequest.getAddress().getBuildingNumber()
        );

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

        List<CompanyHours> openingHours = companyRequest.getOpeningHours()
                .stream()
                .map( request -> CompanyHoursMapper.requestToCompanyHours(request,company)
                ).toList();
        company.assignCompanyHours(openingHours);

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

    public Page<EmployeeDetailsResponse> findEmployeesDetailsByCompanyId(int companyId, String firstName, String lastName, Pageable pageable) {
        Page<CompanyUserRole> companyUserRoles = this.companyUserRoleRepository.findAllByCompanyId(companyId, firstName, lastName, pageable);
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

    // Method for dismiss employee from company
    public void dismissEmployeeFromCompany(int companyId, int employeeId) {
        CompanyUserRole companyUserRole = this.companyUserRoleRepository.findByUserIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Employee not found in company"));
        this.companyUserRoleRepository.delete(companyUserRole);
        logger.info("Employee was dismissed from Company[{}]", companyUserRole.getCompany().getName());
    }

    // Method to hire new employees to company based on user ids
    public List<EmployeeDetailsResponse> hireEmployeeToCompany(int companyId, EmployeeHireRequest body) {

        for (Integer employeeId : body.getEmployeeIds()) {
            Optional<CompanyUserRole> cur = this.companyUserRoleRepository.findByUserIdAndCompanyId(employeeId, companyId);
            if(cur.isPresent()) {
                UserData user = cur.get().getUser().getUserData();
                throw new EmployeeAlreadyHireException(String.format("Employee %s %s already hired", user.getFirstName(), user.getLastName()));
            }
        }

        CompanyRole employeeRole = this.companyRoleRepository.findByName("COMPANY_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Company role not found"));

        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<EmployeeDetailsResponse> newHiredEmployees = new ArrayList<>();

        for(Integer employeeId : body.getEmployeeIds()) {
            User newEmployee = this.userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CompanyUserRole companyUserRole = this.companyUserRoleRepository.save(new CompanyUserRole(newEmployee, company, employeeRole));
            EmployeeDetailsResponse response = UserMapper.toEmployeeDetailsResponse(companyUserRole.getUser(), companyUserRole.getRole().getName());
            newHiredEmployees.add(response);
        }
        logger.info("New employee [{}] was assign to company[{}]",newHiredEmployees.size(), company.getName());
        return newHiredEmployees;
    }

    // Method to update company name or logo based on company id.
    public CompanyDetailsResponse updateCompanyNameOrLogo(int companyId, CompanyProfileRequest body, MultipartFile logo) {
        Company company = this.getCompany(companyId);

        if(logo != null) {
            try{
                byte[] logoBytes = logo.getBytes();
                company.assignLogo(logoBytes);
            } catch (IOException exception){
                throw new ImageException(exception.getMessage());
            }
        }

        if(body != null && !body.getName().isEmpty()){
            String newCompanyName = body.getName();
            company.setName(newCompanyName);
        }

        List<Object[]> reviews = this.reviewRepository.findAllByCompanyId(companyId);
        ReviewStatisticsResponse reviewStatisticsResponse = ReviewMapper.reviewsToReviewsResponse(reviews);

        return CompanyMapper.companyToCompanyDetailsResponse(this.companyRepository.save(company), reviewStatisticsResponse);
    }

    // Method to update company description based on company id.
    public CompanyDetailsResponse updateCompanyDescription(@Positive(message = "Company id must be positive number") int companyId, @Valid CompanyDescriptionRequest companyDescriptionRequest) {
        Company company = this.getCompany(companyId);

        if(companyDescriptionRequest != null &&
                company.getDescription().length() != companyDescriptionRequest.getDescription().length()) {
            company.setDescription(companyDescriptionRequest.getDescription());
        }

        List<Object[]> reviews = this.reviewRepository.findAllByCompanyId(companyId);
        ReviewStatisticsResponse reviewStatisticsResponse = ReviewMapper.reviewsToReviewsResponse(reviews);

        return CompanyMapper.companyToCompanyDetailsResponse(this.companyRepository.save(company), reviewStatisticsResponse);
    }

    public AddressResponse updateCompanyAddress(int companyId, AddressRequest addressRequest) {
        Company company = this.getCompany(companyId);

        Address oldAddress = company.getAddress();
        oldAddress.update(addressRequest);

        this.companyRepository.save(company);

        return AddressMapper.addressToAddressResponse(oldAddress);
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

    private void validateNoDuplicateDays(List<CompanyHoursRequest> list) {
        Set<String> days = new HashSet<>();

        for (CompanyHoursRequest item : list) {
            String day = item.getDayOfWeek();

            if (!days.add(day)) {
                throw new BadRequestException("Duplicate days found: " + day);
            }
        }
    }

}
