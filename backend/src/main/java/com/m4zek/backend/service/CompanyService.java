package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.write.CompanyDescriptionRequest;
import com.m4zek.backend.model.dto.write.CompanyRequest;
import com.m4zek.backend.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;


    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }


    public Optional<Company> findCompanyById(long companyId){
        return this.companyRepository.findById(companyId);
    }

    public Company findCompanyByIdOrElseThrow(long companyId){
        return this.findCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
    }

    public Company save(Company company){
        company = this.companyRepository.save(company);
        logger.info("Company was saved [{}]", company.getId());
        return this.companyRepository.save(company);
    }

    public Company createCompany(CompanyRequest request, Category category) {
        return new Company(
                request.getName(),
                request.getDescription(),
                category,
                null
        );
    }

    public Company updateCompanyName(Company company, String name){
        if(name != null && !name.isEmpty()){
            company.setName(name);
            logger.info("Company [{}] name was changed: {}", company.getId(), company.getName());
        }
        return company;
    }

    public Company updateCompanyDescription(Company company, CompanyDescriptionRequest request){
        if(request != null && company.getDescription().length() != request.getDescription().length()) {
            company.setDescription(request.getDescription());
            logger.info("Company [{}] description was changed: {}", company.getId(), company.getDescription());
        }
        return company;
    }

    public long getTotalCompaniesCount() {
        return this.companyRepository.countAllCompanies();
    }

    public double getCompanyGrowthPercentage() {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDateTime now = LocalDateTime.now(zone);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime sixtyDaysAgo = now.minusDays(60);

        long periodA = companyRepository.countCompaniesCreatedBetween(thirtyDaysAgo, now);
        long periodB = companyRepository.countCompaniesCreatedBetween(sixtyDaysAgo, thirtyDaysAgo);


        if (periodB == 0) {
            return periodA > 0 ? 100.0 : 0.0;
        }

        double growth = ((double) periodA - periodB) / periodB * 100.0;

        return Math.round(growth * 10.0) / 10.0;
    }


    public Page<Company> findTopCompaniesByReservation(Pageable pageable){
        return this.companyRepository.findCompaniesAndSortByReservationCount(pageable);
    }

}
