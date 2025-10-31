package com.m4zek.backend.service;

import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.CompanyReadModel;
import com.m4zek.backend.model.dto.write.CompanyWriteModel;
import com.m4zek.backend.model.dto.read.UserReadModel;
import com.m4zek.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final CompanyUserRoleRepository companyUserRoleRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, CategoryRepository categoryRepository, CompanyUserRoleRepository companyUserRoleRepository, CompanyRoleRepository companyRoleRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
        this.companyUserRoleRepository = companyUserRoleRepository;
        this.companyRoleRepository = companyRoleRepository;
        this.userRepository = userRepository;
    }

    public Company saveCompany(CompanyWriteModel companyWriteModel) {
        Category category = this.getCategory(companyWriteModel.getCategory().getName());
        Address address = companyWriteModel.getAddress().toEntity();

        byte[] logoBytes = companyWriteModel.getLogo() != null ?
                Base64.getDecoder().decode(companyWriteModel.getLogo()) : null;

        Company newCompany = new Company(
                companyWriteModel.getName(),
                companyWriteModel.getDescription(),
                logoBytes,
                category,
                address
        );

        address.assignCompany(newCompany);
        newCompany = this.companyRepository.save(newCompany);

        this.assignOwnerToCompany(companyWriteModel.getOwner_id(), newCompany);

        return newCompany;
    }

    public CompanyReadModel readCompany(int companyId){
        return this.getCompany(companyId).toReadModel();
    }


    @Transactional
    public Company updateCompany(int companyId, CompanyWriteModel companyWriteModel) {
        Company company = this.getCompany(companyId);

        if(companyWriteModel.getName() != null && !companyWriteModel.getName().isEmpty())
            company.changeName(companyWriteModel.getName());

        if(companyWriteModel.getDescription() != null && !companyWriteModel.getDescription().isEmpty())
            company.changeDescription(companyWriteModel.getDescription());

        if(companyWriteModel.getLogo() != null && !companyWriteModel.getLogo().isEmpty()) {
            byte[] logoBytes = Base64.getDecoder().decode(companyWriteModel.getLogo());
            company.changeLogo(logoBytes);
        }

        if(companyWriteModel.getCategory() != null){
            Category category = this.getCategory(companyWriteModel.getCategory().getName());
            company.assignCategory(category);
        }

        companyRepository.save(company);
        return company;
    }

    /*
        TODO Create a separate DTO EmployeeReadModel to return
            information about employees and their roles in the company?
     */
    public List<UserReadModel> readAllCompanyEmployees(int companyId) {
        List<CompanyUserRole> companyUserRoles = this.companyUserRoleRepository.findAllByCompanyId(companyId);

        return companyUserRoles.stream()
                .map(item -> item.getUsers().toUserReadModel()).toList();
    }


    public Page<CompanyReadModel> readAllCompanies(Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        List<CompanyReadModel> companyReadModels = companies.stream()
                .map(Company::toReadModel)
                .toList();
        return new PageImpl<>(companyReadModels, pageable, companies.getTotalElements());
    }



    public void deleteCompany(int companyId) {
        Company companyToDelete = getCompany(companyId);
        this.companyRepository.delete(companyToDelete);
    }


    // Private methods
    private Company getCompany(int companyId) {
        return this.companyRepository.findById(companyId)
                .orElseThrow(()-> new CompanyNotFoundException("Company with id " + companyId + " not found"));
    }

    private Category getCategory(String categoryName) {
        return this.categoryRepository.findByName(categoryName).orElseThrow(
                () -> new CategoryNotFoundException("Category with given name not found")
        );
    }

    private void assignOwnerToCompany(int user_id, Company company) {
        User user = userRepository.findById(user_id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        CompanyRole ownerCompanyRole = this.companyRoleRepository.findByName("COMPANY_OWNER")
                .orElseThrow(() -> new CompanyNotFoundException("Company role not found"));


        CompanyUserRole ownerRole = new CompanyUserRole(user, company, ownerCompanyRole);
        this.companyUserRoleRepository.save(ownerRole);
    }


}
