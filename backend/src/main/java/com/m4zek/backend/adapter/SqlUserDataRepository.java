package com.m4zek.backend.adapter;

import com.m4zek.backend.model.UserData;
import com.m4zek.backend.repository.UserDataRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlUserDataRepository extends UserDataRepository, JpaRepository<UserData, Integer> {
}
