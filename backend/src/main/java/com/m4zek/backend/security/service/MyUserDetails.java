package com.m4zek.backend.security.service;

import com.m4zek.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MyUserDetails implements UserDetails {

    private int id;
    private String addressEmail;
    private String password;
    private Boolean isBlock;
    private Boolean isEnable;
    private int userDataId;
    private Collection<? extends GrantedAuthority> authorities;

    public MyUserDetails(int id, String addressEmail,
                         String password, Boolean isBlock,
                         Boolean isEnable, int userDataId,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.addressEmail = addressEmail;
        this.password = password;
        this.isBlock = isBlock;
        this.isEnable = isEnable;
        this.userDataId = userDataId;
        this.authorities = authorities;
    }

    public static MyUserDetails build(User user) {
        return user.toMyUserDetails();
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

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isBlock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnable;
    }

}

