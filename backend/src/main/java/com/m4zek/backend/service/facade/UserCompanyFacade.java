package com.m4zek.backend.service.facade;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.EmployeeAlreadyHireException;
import com.m4zek.backend.mapper.CompanyMapper;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyRole;
import com.m4zek.backend.model.CompanyUserRole;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.EmployeeDetailsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.read.UserCompanyResponse;
import com.m4zek.backend.model.dto.write.EmployeeHireRequest;
import com.m4zek.backend.model.dto.write.UserCompanyRoleRequest;
import com.m4zek.backend.service.CompanyService;
import com.m4zek.backend.service.UserCompanyService;
import com.m4zek.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserCompanyFacade {

    private final static Logger logger = LoggerFactory.getLogger(UserCompanyFacade.class);

    private final UserService userService;
    private final UserCompanyService userCompanyService;
    private final CompanyService companyService;

    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;

    public UserCompanyFacade(UserService userService, UserCompanyService userCompanyService, CompanyService companyService, CompanyMapper companyMapper, UserMapper userMapper) {
        this.userService = userService;
        this.userCompanyService = userCompanyService;
        this.companyService = companyService;
        this.companyMapper = companyMapper;
        this.userMapper = userMapper;
    }


    // Find user companies (Own company and company where works)
    public List<UserCompanyResponse> findUserCompanies(){
        // Find logged user
        User loggedUser = this.userService.findLoggedUser();

        // Get all user roles in companies
        List<CompanyUserRole> roles = this.userCompanyService.findAllUserRolesInCompanies(loggedUser);

        // Group roles by company for user
        Map<Company, List<CompanyUserRole>> grouped = roles.stream()
                .collect(Collectors.groupingBy(CompanyUserRole::getCompany));

        // Return Mapping grouped into UserCompanyResponse - object handling data about user company
        return grouped.entrySet().stream()
                .map(entry -> {
                    Company company = entry.getKey();
                    List<CompanyUserRole> userRolesInCompany = entry.getValue();
                    List<String> roleNames = userRolesInCompany.stream()
                            .map(role -> role.getRole().getName()).toList();
                    return this.companyMapper.companyToUserCompanyResponse(company, roleNames);
                }).toList();
    }

    // Find company employees
    public List<EmployeeSummaryResponse> findCompanyEmployees(int companyId){
        // Find company based on company id
        Company company = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with given id not found"));

        // Mapping from roles -> user into summary employee data list
        List<EmployeeSummaryResponse> employees = company.getUsers().stream()
                .map(role -> this.userMapper.toEmployeeSummaryResponse(role.getUser()))
                .toList();

        return employees;
    }

    // Find Company employees
    public Page<EmployeeDetailsResponse> findCompanyEmployeesDetails(int companyId, String firstName, String lastName, Pageable pageable){
        Page<CompanyUserRole> companyUserRoles = this.userCompanyService.findEmployeesByFirstNameAndLastName(
                companyId, firstName, lastName, pageable);

        List<EmployeeDetailsResponse> employees = companyUserRoles.stream()
                .map(role -> this.userMapper.toEmployeeDetailsResponse(role.getUser(), role.getRole().getName()))
                .toList();

        return new PageImpl<>(employees, pageable, companyUserRoles.getTotalElements());
    }

    // Method to update user role in company
    public EmployeeDetailsResponse changeEmployeeRoleInCompany(int companyId, int employeeId, UserCompanyRoleRequest request){

        // Find relation user <-> company role
        CompanyUserRole companyUserRole = this.userCompanyService.findByUserIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Employee with id " + employeeId + " not found in company with id" + companyId));

        // Get old and new role name
        String oldRoleName = companyUserRole.getRole().getName();
        String newRoleName = request.getRole().toString();

        // Find company role based on new role name
        CompanyRole newRole = this.userCompanyService.findCompanyRole(newRoleName)
                .orElseThrow(() -> new CompanyNotFoundException("Role with name " + newRoleName + " not found"));

        // Assigned new role
        companyUserRole.assignRole(newRole);

        // Save changes
        CompanyUserRole saved = this.userCompanyService.save(companyUserRole);

        logger.info("Employee[{}] in Company[{}] has change role from [{}] to [{}]", employeeId, companyId, oldRoleName, newRoleName);

        return this.userMapper.toEmployeeDetailsResponse(saved.getUser(), newRole.getName());
    }


    // Method to dismiss employe from company
    public void dismissEmployee(int employeeId, int companyId){
        // Find relation user <-> company
        CompanyUserRole relation = this.userCompanyService.findByUserIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Employee not found in company"));

        // Delete relation = Dismiss employee from company
        this.userCompanyService.delete(relation);
        logger.info("Employee [{}] has been dismissed from Company [{}]", relation.getUser().getId(), relation.getCompany().getId());
    }

    @Transactional
    public List<EmployeeDetailsResponse> hireEmployee(
            int companyId,
            EmployeeHireRequest request
    ) {

        // Find company
        Company company = companyService.findCompanyById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        // Get employee role
        CompanyRole employeeRole = userCompanyService
                .findCompanyRole("COMPANY_EMPLOYEE")
                .orElseThrow(() -> new CompanyNotFoundException("Role not found"));

        // Create empty list for return values
        List<EmployeeDetailsResponse> result = new ArrayList<>();

        // Iterable for request ids
        for (Integer employeeId : request.getEmployeeIds()) {

            // If employee already is hired to company throw exception
            if (userCompanyService.employeeHiredInCompany(employeeId, companyId)) {
                throw new EmployeeAlreadyHireException("Employee already hired in Company");
            }

            // Find employee
            User employee = userService.findUserById(employeeId);

            // Create relation user <-> company - Add employee role into user - company
            CompanyUserRole relation =
                    userCompanyService.createAndSaveRelation(
                            employee,
                            company,
                            employeeRole
                    );

            result.add(
                    userMapper.toEmployeeDetailsResponse(
                            relation.getUser(),
                            relation.getRole().getName()
                    )
            );
        }

        return result;
    }


}
