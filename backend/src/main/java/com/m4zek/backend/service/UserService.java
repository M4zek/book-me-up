package com.m4zek.backend.service;

import com.m4zek.backend.exception.EmailExistsException;
import com.m4zek.backend.exception.RefreshTokenException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.RefreshToken;
import com.m4zek.backend.model.Role;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.repository.RefreshTokenRepository;
import com.m4zek.backend.repository.RoleRepository;
import com.m4zek.backend.repository.UserDateRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${jwe.token.refreshExpirationSec}")
    private Long refreshTokenExpiration;

    private final UserDateRepository userDateRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserDateRepository userDateRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userDateRepository = userDateRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public UserResponse findLoggedInUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedUser = findUserByEmail(myUserDetails.getEmail());
        return UserMapper.toUserResponse(loggedUser);
    }


    public UserResponse createNewUser(UserRequest userRequest) {
        String userEmail = userRequest.getAddressEmail();

        if(this.userRepository.existsByAddressEmail(userEmail)) {
            throw new EmailExistsException("Address email already exists");
        }

        UserData userData = userRequest.getUserData().toEntity();
        userData = this.userDateRepository.save(userData);

        Set<Role> roles = new HashSet<>();
        roles.add(this.roleRepository.findByName("ROLE_USER").get());


        User userToCreate = new User(
                userRequest.getAddressEmail(),
                passwordEncoder.encode(userRequest.getPassword()),
                userData,
                roles
        );

        User savedUser = this.userRepository.save(userToCreate);

        logger.info("[UserService] User created successfully");
        return UserMapper.toUserResponse(savedUser);
    }


    private User findUserByEmail(String email) {
        return this.userRepository.findByAddressEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email [" + email + "] not found")
        );
    }


    public String createRefreshToken(int user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new UserNotFoundException("User with given id does not exists"));

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(user_id);
        optionalRefreshToken.ifPresent(refreshTokenRepository::delete);

        String newRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        Instant.now().plusMillis(refreshTokenExpiration * 1000),
                        user
                )
        );

        logger.info("[UserService] Refresh token created successfully");
        return refreshToken.getRefreshToken();
    }


    public Optional<RefreshToken> findRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpireDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException(refreshToken.getRefreshToken(), "Session has expired, please log in again!");
        }
        return refreshToken;
    }

    public RefreshToken updateExpirationTime(RefreshToken refreshToken){
        if(refreshToken.getExpireDate().compareTo(Instant.now()) > 0) {
            refreshToken.setExpireDate(Instant.now().plusMillis(refreshTokenExpiration * 1000));
            refreshToken = refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public User findUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with given id doesn't exists"));
    }
}
