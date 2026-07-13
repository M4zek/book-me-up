package com.m4zek.backend.service;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanySearchService {

    private final CompanyRepository companyRepository;

    public CompanySearchService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }


    // Search companies by name, category
    public Page<Company> searchRecommendedCompanies(String city, String category, Pageable pageable){
        boolean hasCity = city != null && !city.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        // Search by city and category
        if (hasCity && hasCategory) {
            return this.companyRepository.findByCityAndCategory(city, category, pageable);
        }

        // Search by city
        if (hasCity) {
            return this.companyRepository.findByCity(city, pageable);
        }

        // Search by category
        if (hasCategory) {
            return this.companyRepository.findByCategory(category, pageable);
        }

        // Search without params - return all
        return this.companyRepository.findAll(pageable);
    }


    // Search companies by city, name or category name
    public Page<Company> searchCompanies(Pageable pageable, String companyName, String city, String categoryName){
        Page<Company> results = this.companyRepository.searchCompanyByNameAndCityAndCategoryName(
                pageable, companyName, city, categoryName
        );
        return results;
    }

    // Find company based on id
    public Optional<Company> findById(int companyId){
        return this.companyRepository.findById(companyId);
    }





}
