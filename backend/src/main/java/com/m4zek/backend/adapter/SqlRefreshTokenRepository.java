package com.m4zek.backend.adapter;

import com.m4zek.backend.model.RefreshToken;
import com.m4zek.backend.repository.RefreshTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlRefreshTokenRepository extends RefreshTokenRepository, JpaRepository<RefreshToken, Integer> {
}
