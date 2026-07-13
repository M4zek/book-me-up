package com.m4zek.backend.service.facade;


import com.m4zek.backend.mapper.CompanyHoursMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.service.CompanyHoursService;
import com.m4zek.backend.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyHoursFacade {

    private final CompanyService companyService;
    private final CompanyHoursService companyHoursService;

    private final CompanyHoursMapper companyHoursMapper;

    public CompanyHoursFacade(CompanyService companyService, CompanyHoursService companyHoursService, CompanyHoursMapper companyHoursMapper) {
        this.companyService = companyService;
        this.companyHoursService = companyHoursService;
        this.companyHoursMapper = companyHoursMapper;
    }



    // Read company business hours (Days with opening hours)
    public List<CompanyHoursResponse> readCompanyBusinessHours(long companyId){

        // Find all company days with opening hours
        List<CompanyHours> hours = this.companyHoursService.findCompanyHours(companyId);

        // Mapped to response
        List<CompanyHoursResponse> result = hours.stream()
                .map(this.companyHoursMapper::companyHoursToCompanyHoursResponse)
                .toList();

        return result;
    }

    public List<CompanyHoursResponse> createCompanyBusinessHours(long companyId, List<CompanyHoursRequest> requests){
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        List<CompanyHours> hours = this.companyHoursService.createAndSaveCompanyHours(company, requests);

        return hours.stream()
                .map(this.companyHoursMapper::companyHoursToCompanyHoursResponse)
                .toList();
    }



    // Method to update company hours
    public List<CompanyHoursResponse> updateCompanyHours(long companyId, List<CompanyHoursRequest> requests){

        // Find company who are assigned to hours
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        // Update hours
        List<CompanyHours> updatedHours = this.companyHoursService.updateHours(company, requests);

        // Mapping to response
        List<CompanyHoursResponse> mappedEntities =
                updatedHours.stream().map(
                        this.companyHoursMapper::companyHoursToCompanyHoursResponse
                ).toList();

        return mappedEntities;
    }

}
