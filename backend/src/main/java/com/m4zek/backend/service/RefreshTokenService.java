package com.m4zek.backend.service;

import com.m4zek.backend.exception.RefreshTokenException;
import com.m4zek.backend.model.RefreshToken;
import com.m4zek.backend.model.User;
import com.m4zek.backend.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    public static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwe.token.refreshExpirationSec}")
    private Long refreshTokenExpiration;

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }


    public RefreshToken generateAndSaveRefreshToken(User user){
        String token = UUID.randomUUID().toString();
        Instant expirationTime = Instant.now().plusMillis(refreshTokenExpiration * 1000);

        RefreshToken savedToken = repository.save(
                new RefreshToken(token, expirationTime, user)
        );

        logger.info("Refresh token [{}] has been created for User [{}]", savedToken.getRefreshToken(), user.getId());
        return savedToken;
    }

    public RefreshToken createRefreshTokenToUser(User user){
        this.findByUserId(user.getId())
                .ifPresent(this::deleteToken);

        return this.generateAndSaveRefreshToken(user);
    }


    public Optional<RefreshToken> findByToken(String token){
        return repository.findByRefreshToken(token);
    }

    public Optional<RefreshToken> findByUserId(int userId){
        return this.repository.findByUserId(userId);
    }


    public void deleteToken(RefreshToken token){
        this.repository.delete(token);
        logger.info("Refresh token [{}] for user [{}] has been deleted", token.getRefreshToken(), token.getUser().getId());
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpireDate().compareTo(Instant.now()) < 0){
            this.deleteToken(refreshToken);
            throw new RefreshTokenException(refreshToken.getRefreshToken(), "Session has expired, please log in again!");
        }
        return refreshToken;
    }

    public RefreshToken updateExpirationTime(RefreshToken refreshToken){
        if(refreshToken.getExpireDate().compareTo(Instant.now()) > 0) {
            refreshToken.setExpireDate(Instant.now().plusMillis(refreshTokenExpiration * 1000));
            refreshToken = this.repository.save(refreshToken);
        }
        return refreshToken;
    }

}
