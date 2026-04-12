package com.m4zek.backend.security.jwt;

import com.m4zek.backend.security.service.MyUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.SecretKeyAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenManager {
    public static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    @Value("${jwe.token.expirationSec}")
    private long EXPIRATION;

    @Value("${jwe.token.header}")
    private String HEADER;

    @Value("${jwe.token.prefix}")
    private String PREFIX;

    private final SecretKeyAlgorithm alg;
    private final SecretKey key;
    private final AeadAlgorithm enc;

    public TokenManager() {
        this.alg = Jwts.KEY.A256GCMKW;
        this.key = alg.key().build();
        this.enc = Jwts.ENC.A256GCM;
    }


    // Generate JWE based on user address email
    public String generateToken(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(myUserDetails.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .encryptWith(key, alg, enc)
                .compact();
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .encryptWith(key, alg, enc)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().decryptWith(key).build().parseEncryptedClaims(token).getPayload().getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().decryptWith(key).build().parseEncryptedClaims(token);
            return true;
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(PREFIX)) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
