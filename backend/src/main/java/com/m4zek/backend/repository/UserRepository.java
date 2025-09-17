package com.m4zek.backend.repository;

import com.m4zek.backend.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByAddressEmail(String email);

    Optional<User> findById(int userId);

    boolean existsByAddressEmail(String email);

    User save(User user);
}
