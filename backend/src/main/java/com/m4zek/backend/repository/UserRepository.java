package com.m4zek.backend.repository;

import com.m4zek.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
