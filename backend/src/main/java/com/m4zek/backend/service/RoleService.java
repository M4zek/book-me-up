package com.m4zek.backend.service;

import com.m4zek.backend.model.Role;
import com.m4zek.backend.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Optional<Role> findRoleUser(){
        return this.roleRepository.findByName("ROLE_USER");
    }


    public Optional<Role> findRoleAdmin(){
        return this.roleRepository.findByName("ROLE_ADMIN");
    }

}
