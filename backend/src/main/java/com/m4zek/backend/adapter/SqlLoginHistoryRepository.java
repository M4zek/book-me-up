package com.m4zek.backend.adapter;


import com.m4zek.backend.model.LoginHistory;
import com.m4zek.backend.repository.LoginHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlLoginHistoryRepository extends LoginHistoryRepository, JpaRepository<LoginHistory, Integer> {
}
