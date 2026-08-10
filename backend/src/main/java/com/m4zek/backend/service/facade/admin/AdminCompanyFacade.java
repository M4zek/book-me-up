package com.m4zek.backend.service.facade.admin;

import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.admin.CompanyRankingResponse;
import com.m4zek.backend.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCompanyFacade {

    private final CompanyService companyService;

    private final CompanyMapper companyMapper;

    public AdminCompanyFacade(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }


    // Method to get companies sorted by reservations
    public Page<CompanyRankingResponse> getTopCompaniesBasedOnReservation(Pageable pageable) {

        // Get companies sorted by reservation
        Page<Company> topCompaniesByReservations = this.companyService.findTopCompaniesByReservation(pageable);

        // Mapping company to company ranking response
        List<CompanyRankingResponse> rankingList = topCompaniesByReservations.stream()
                .map(company -> {
                    // Count reservation
                    long reservationCount = company.getCompanyOffers()
                            .stream()
                            .mapToLong(c -> c.getReservations().size())
                            .sum();
                    // Return item to list (Mapped into CompanyRankingResponse)
                    return this.companyMapper.companyToCompanyRankingResponse(company, reservationCount);
                }).toList();

        return new PageImpl<>(rankingList, pageable, topCompaniesByReservations.getTotalElements());
    }
}
