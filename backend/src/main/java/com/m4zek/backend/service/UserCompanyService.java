package com.m4zek.backend.service;


import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyUserRole;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.repository.CompanyUserRoleRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserCompanyService {

    private final UserRepository userRepository;
    private final CompanyUserRoleRepository companyUserRoleRepository;

    public UserCompanyService(UserRepository userRepository, CompanyUserRoleRepository companyUserRoleRepository) {
        this.userRepository = userRepository;
        this.companyUserRoleRepository = companyUserRoleRepository;
    }


//    PUBLIC METHODS
    public List<UserCompanyResponse> findAllUserCompanies() {
        User loggedUser = this.findLoggedInUser();
        List<CompanyUserRole> roles = this.companyUserRoleRepository.findAllByUserId(loggedUser.getId());

        Map<Company, List<CompanyUserRole>> grouped =   roles.stream().collect(Collectors.groupingBy(CompanyUserRole::getCompany));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Company company = entry.getKey();
                    List<CompanyUserRole> companyUserRoles = entry.getValue();

                    List<String> rolesName = companyUserRoles.stream().map(r -> r.getRole().getName()).toList();

                    return CompanyMapper.companyToUserCompanyResponse(company, rolesName);
                }).toList();
    }



//    PRIVATE METHODS
    private User findLoggedInUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userRepository.findById(myUserDetails.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
