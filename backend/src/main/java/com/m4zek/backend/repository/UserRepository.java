package com.m4zek.backend.repository;

import com.m4zek.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByAddressEmail(String email);

    Optional<User> findById(int userId);

    boolean existsByAddressEmail(String email);

    User save(User user);


    @Query("""
        SELECT u from users u
        JOIN u.userData ud
         WHERE (:firstName IS NULL OR LOWER(ud.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
              AND (:lastName IS NULL OR LOWER(ud.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
    """)
    Page<User> findAllByFirstNameAndLastname(Pageable pageable,
                                             @Param("firstName") String firstName,
                                             @Param("lastName") String lastName);

    List<User> findAllByIdIn(List<Integer> ids);


    @Query("""
            SELECT COUNT(u) FROM users u
    """)
    long countAllUsers();


    @Query("""
           SELECT COUNT(u) FROM users u
               WHERE u.createdDate >= :startDate
                   AND u.createdDate < :endDate
    """)
    long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);



    @Query(value = """
        SELECT
            DATE_FORMAT(u.created_date, '%M'),
            COUNT(*)
        FROM users u
        WHERE u.created_date >= :from
          AND u.created_date < :to
        GROUP BY DATE_FORMAT(u.created_date, '%M')
        ORDER BY DATE_FORMAT(u.created_date, '%M')
        """, nativeQuery = true)
    List<Object[]> countGrowthBetweenDate(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT u from users u
            ORDER BY u.createdDate DESC
    """
    )
    Page<User> findLastUserCreated(Pageable pageable);


    @Query("""
        SELECT DISTINCT u.id
            FROM users u
            JOIN u.roles r
            WHERE r.name LIKE 'ROLE_ADMIN_VIEWER'
                    OR r.name LIKE 'ROLE_ADMIN'
        """)
    List<Integer> findAdminIds();

//    @Query("""
//        SELECT DISTINCT u FROM users u
//        WHERE :tokens IS NULL OR (
//            LOWER(u.userData.firstName) LIKE CONCAT('%', :tokens, '%') OR
//            LOWER(u.userData.lastName) LIKE CONCAT('%', :tokens, '%') OR
//            LOWER(u.addressEmail) LIKE CONCAT('%', :tokens, '%')
//        )
//    """)
//    Page<User> findUsersByNameOrStatusOrRoles(
//            Pageable pageable,
//            @Param("tokens") List<String> tokens,
//            @Param("roles") List<RoleEnum> roles,
//            @Param("statuses") List<UserStatus> statuses
//    );

    Page<User> findAll(
            Specification<User> spec,
            Pageable pageable
    );

}
