package com.m4zek.backend.security.service;

import com.m4zek.backend.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;


public class MyUserDetails implements UserDetails {

    private int id;
    private String addressEmail;
    private String password;
    private UserStatus status;
    private LocalDateTime suspendedTo;
    private Collection<? extends GrantedAuthority> authorities;

    public MyUserDetails(int id, String addressEmail,
                         String password, UserStatus status, LocalDateTime suspendedTo,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.addressEmail = addressEmail;
        this.password = password;
        this.status = status;
        this.suspendedTo = suspendedTo;
        this.authorities = authorities;
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return addressEmail;
    }

    public UserStatus getStatus(){
        return this.status;
    }

    public LocalDateTime getSuspendedTo(){
        return this.suspendedTo;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return null;
    }


    @Override
    public boolean isAccountNonLocked() {
        return switch (status) {
            case BLOCK -> false;
            case SUSPENDED -> suspendedTo != null &&
                            !LocalDateTime.now().isBefore(suspendedTo);
            default -> true;
        };
    }

    @Override
    public boolean isEnabled() {
        return this.status != UserStatus.NOT_ACTIVE;
    }

}

