package com.m4zek.backend.service;


import com.m4zek.backend.model.CompanyRole;
import com.m4zek.backend.repository.CompanyRoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyRoleService {

    private final CompanyRoleRepository repository;

    public CompanyRoleService(CompanyRoleRepository repository) {
        this.repository = repository;
    }

    public Optional<CompanyRole> findByName(String name){ return this.repository.findByName(name);}

    public Optional<CompanyRole> findRoleEmployee(){
        return this.repository.findByName("COMPANY_EMPLOYEE");
    }

    public Optional<CompanyRole> findRoleManager(){ return this.repository.findByName("COMPANY_MANAGER");}

    public Optional<CompanyRole> findRoleOwner(){
        return this.repository.findByName("COMPANY_OWNER");
    }

}
