package com.m4zek.backend.adapter;

import com.m4zek.backend.model.User;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SqlUserRepository extends UserRepository, JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
}
