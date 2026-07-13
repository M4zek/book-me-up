package com.m4zek.backend.service.facade;

import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.ReviewMapper;
import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.CompanyDetailsResponse;
import com.m4zek.backend.model.dto.read.ReviewStatisticsResponse;
import com.m4zek.backend.model.dto.write.CompanyDescriptionRequest;
import com.m4zek.backend.model.dto.write.CompanyProfileRequest;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CompanyFacade {

    private final UserService userService;
    private final CompanyService companyService;
    private final AddressService addressService;
    private final CompanyHoursService hoursService;
    private final CategoryService categoryService;
    private final UserCompanyService userCompanyService;
    private final FileManagementFacade fileManagementFacade;
    private final ReviewService reviewService;

    private final CompanyMapper companyMapper;
    private final ReviewMapper reviewMapper;


    public CompanyFacade(UserService userService, CompanyService companyService, AddressService addressService, CompanyHoursService hoursService, CategoryService categoryService, UserCompanyService userCompanyService, FileManagementFacade fileManagementFacade, ReviewService reviewService, CompanyMapper companyMapper, ReviewMapper reviewMapper) {
        this.userService = userService;
        this.companyService = companyService;
        this.addressService = addressService;
        this.hoursService = hoursService;
        this.categoryService = categoryService;
        this.userCompanyService = userCompanyService;
        this.fileManagementFacade = fileManagementFacade;
        this.reviewService = reviewService;
        this.companyMapper = companyMapper;
        this.reviewMapper = reviewMapper;
    }



    @Transactional
    public CompanyDetailsResponse createCompany(
            CompanyRequest request, MultipartFile logo)
    {
        // Find owner company or throw exception
        User owner = userService.findUserById(request.getOwner_id());

        // Find company category or throw exception
        Category category = categoryService.findByName(request.getCategory().getName())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category with given name not found"
                        ));

        // Create company with based data
        Company company = companyService.createCompany(
                request, category
        );

        // Create address and assign to company
        addressService.assignAddress(
                company, request.getAddress()
        );

        // Create relation user <-> company. Which means create relation user owner to company
        userCompanyService.assignOwner(
                owner, company
        );

        // Create company hours and assign it to company
        hoursService.assignOpeningHours(
                company, request.getOpeningHours()
        );

        // If logo exists create stored file save into db and save file into minio.
        fileManagementFacade.assignLogoIfPresentToCompany(
                company, logo
        );

        // Save company
        company = companyService.save(company);

        // Map company into details dto and return with empty statistics
        return companyMapper.companyToCompanyDetailsResponse(
                company, ReviewStatisticsResponse.builder().build()
        );
    }


    // Method to update company logo and/or name
    @Transactional
    public CompanyDetailsResponse updateCompanyProfile(int companyId, CompanyProfileRequest data, MultipartFile logo){
        // Find company based on id or throw exception company not found
        Company company = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // Update Company name
        company = companyService.updateCompanyName(company, data.getName());

        // Assign new logo if present
        fileManagementFacade.assignLogoIfPresentToCompany(company, logo);

        // Save changes
        company = companyService.save(company);

        // Read company reviews statistics for DTO
        List<Object[]> reviews = this.reviewService.findCompanyReviewsData(companyId);
        ReviewStatisticsResponse reviewStatisticsResponse = this.reviewMapper.reviewsToReviewsResponse(reviews);

        // Return DTO with Company Details
        return this.companyMapper.companyToCompanyDetailsResponse(company, reviewStatisticsResponse);
    }


    @Transactional
    public CompanyDetailsResponse updateCompanyDetails(int companyId, CompanyDescriptionRequest request){
        // Find company based on id or throw exception company not found
        Company company = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // Update company description if has been changed
        company = this.companyService.updateCompanyDescription(company, request);

        // Save changes into company
        company = companyService.save(company);

        // Read company reviews statistics for DTO
        List<Object[]> reviews = this.reviewService.findCompanyReviewsData(companyId);
        ReviewStatisticsResponse reviewStatisticsResponse = this.reviewMapper.reviewsToReviewsResponse(reviews);

        // Return DTO with Company Details
        return this.companyMapper.companyToCompanyDetailsResponse(company, reviewStatisticsResponse);
    }


}
