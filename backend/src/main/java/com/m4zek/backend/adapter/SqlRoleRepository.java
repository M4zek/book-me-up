package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Role;
import com.m4zek.backend.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlRoleRepository extends RoleRepository, JpaRepository<Role, Integer> {
}
