package com.m4zek.backend.service;


import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyUserRole;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.dto.read.UserToHiredResponse;
import com.m4zek.backend.repository.CompanyUserRoleRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    // Method to search users to hire based od first name and last name.
    public Page<UserToHiredResponse> findUsersToHireByFirstNameAndSurname(Pageable pageable, String firstName, String lastName) {
        Page<User> users = this.userRepository.findAllByFirstNameAndLastname(pageable, firstName, lastName);

        List<UserToHiredResponse> usersToHireList = users.stream()
                .map(UserMapper::userToUserToHiredResponse)
                .toList();

        return new PageImpl<>(usersToHireList, pageable, users.getTotalElements());
    }

//    PRIVATE METHODS
    private User findLoggedInUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userRepository.findById(myUserDetails.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
