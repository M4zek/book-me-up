package com.m4zek.backend.service;


import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyRole;
import com.m4zek.backend.model.CompanyUserRole;
import com.m4zek.backend.model.User;
import com.m4zek.backend.repository.CompanyUserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserCompanyService {

    private final static Logger logger = LoggerFactory.getLogger(UserCompanyService.class);

    private final CompanyUserRoleRepository companyUserRoleRepository;
    private final CompanyRoleService roleService;

    public UserCompanyService(CompanyUserRoleRepository companyUserRoleRepository, CompanyRoleService roleService) {
        this.companyUserRoleRepository = companyUserRoleRepository;
        this.roleService = roleService;
    }

    @Transactional
    public CompanyUserRole createAndSaveRelation(User employee, Company company, CompanyRole emplRole){
        CompanyUserRole relation =
                new CompanyUserRole(employee, company, emplRole);
        return this.save(relation);
    }

    public CompanyUserRole save(CompanyUserRole companyUserRole){
        CompanyUserRole relation = this.companyUserRoleRepository.save(companyUserRole);
        logger.info("New Employee [{}] has been assigned to Company [{}]",
                relation.getUser().getId(), relation.getCompany().getId());
        return relation;
    }


    @Transactional
    public void assignOwner(User owner, Company company){
        CompanyRole ownerRole = this.roleService.findRoleOwner()
                .orElseThrow(() -> new CompanyNotFoundException("Company role not found"));

        CompanyUserRole relation = this.createAndSaveRelation(owner, company, ownerRole);
        company.addUserRole(relation);
    }

    public List<CompanyUserRole> findAllUserRolesInCompanies(User user){
        return this.companyUserRoleRepository.findAllByUserId(user.getId());
    }

    public Page<CompanyUserRole> findEmployeesByFirstNameAndLastName(int companyId, String firstName, String lastName, Pageable pageable){
        return this.companyUserRoleRepository.findAllByCompanyId(companyId, firstName,lastName,pageable);
    }

    public Optional<CompanyUserRole> findByUserIdAndCompanyId(int userId, int companyId){
        return this.companyUserRoleRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    public void delete(CompanyUserRole companyUserRole){
        this.companyUserRoleRepository.delete(companyUserRole);
    }

    public boolean hasAnyRoleInCompany(Integer company_id, Integer user_id, List<String> roleNames) {
        return this.companyUserRoleRepository.existsByUserIdAndCompanyIdAndRoleNameIn(user_id, company_id, roleNames);
    }

    public boolean employeeHiredInCompany(int employeeId, int companyId){
        return this.companyUserRoleRepository.existsByUserIdAndCompanyId(employeeId, companyId);
    }


    public Optional<CompanyRole> findCompanyRole(String name){
        return this.roleService.findByName(name);
    }

}
