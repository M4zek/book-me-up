package com.m4zek.backend.service.facade;

import com.m4zek.backend.exception.EmailExistsException;
import com.m4zek.backend.exception.RefreshTokenException;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.RefreshToken;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.AuthResponse;
import com.m4zek.backend.model.dto.read.RefreshTokenResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.write.LoginRequest;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.security.jwt.TokenManager;
import com.m4zek.backend.security.service.MyUserDetails;
import com.m4zek.backend.service.RefreshTokenService;
import com.m4zek.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthFacade {

    private final static Logger logger = LoggerFactory.getLogger(AuthFacade.class);

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private final TokenManager tokenManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthFacade(AuthenticationManager authenticationManager,
                      UserMapper userMapper,
                      TokenManager tokenManager,
                      UserService userService,
                      RefreshTokenService refreshTokenService){
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    // Create new user account
    @Transactional
    public UserResponse createAccount(UserRequest userRequest){
        if(this.userService.userExistsByEmail(userRequest.getAddressEmail())){
            throw new EmailExistsException("Address email already exists");
        }

        User createdUser = this.userService.createAndSaveUser(userRequest);

        logger.info("User account has been created [{}]", createdUser.getAddressEmail());
        return this.userMapper.toUserResponse(createdUser);
    }


    public AuthResponse authenticate(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jweToken = tokenManager.generateToken(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User loggedUser = this.userService.findUserById(userDetails.getId());
        RefreshToken refreshToken = this.refreshTokenService.createRefreshTokenToUser(loggedUser);

        AuthResponse authResponse = new AuthResponse(
                userDetails.getId(),
                jweToken,
                refreshToken.getRefreshToken(),
                roles
        );
        logger.info("User authenticated successfully [{}]", userDetails.getEmail());
        return authResponse;
    }

    public RefreshTokenResponse refreshToken(String token){
        RefreshToken refreshToken = this.refreshTokenService.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException(token, "Invalid refresh token"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        refreshToken = refreshTokenService.updateExpirationTime(refreshToken);

        String jwe = tokenManager.generateToken(
                refreshToken.getUser().getAddressEmail()
        );

        logger.info("Refresh token has been refreshed to user [{}]", refreshToken.getUser().getAddressEmail());
        return new RefreshTokenResponse(jwe, refreshToken.getRefreshToken());
    }
}
