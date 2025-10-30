package com.m4zek.backend.repository;

import com.m4zek.backend.model.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
}
