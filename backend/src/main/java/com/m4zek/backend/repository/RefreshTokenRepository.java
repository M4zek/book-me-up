package com.m4zek.backend.repository;

import com.m4zek.backend.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken refreshToken);

    Optional<RefreshToken> findByUserId(int userId);

    void deleteByUserId(int userId);

    Optional<RefreshToken> findByRefreshToken(String token);
}
