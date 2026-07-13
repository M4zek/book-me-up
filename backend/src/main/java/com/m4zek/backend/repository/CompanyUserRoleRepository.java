package com.m4zek.backend.repository;

import com.m4zek.backend.model.CompanyUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyUserRoleRepository {
    CompanyUserRole save(CompanyUserRole companyUserRole);

    Optional<CompanyUserRole> findByUserIdAndCompanyId(int user_id, int company_id);

    Optional<CompanyUserRole> findByCompanyId(int company_id);

    @Query("""
        SELECT cur
        FROM company_user_roles cur
        JOIN cur.user u
        JOIN u.userData ud
        WHERE cur.company.id = :companyId
        AND (:firstName IS NULL OR LOWER(ud.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
        AND (:lastName IS NULL OR LOWER(ud.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
    """)
    Page<CompanyUserRole> findAllByCompanyId(
            @Param("companyId") int companyId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable);

    List<CompanyUserRole> findAllByUserId(int userId);

    boolean existsByUserIdAndCompanyIdAndRoleNameIn(int user_id, int company_id, List<String> roles);

    void delete(CompanyUserRole companyUserRole);

    boolean existsByUserIdAndCompanyId(int employeeId, int companyId);
}
