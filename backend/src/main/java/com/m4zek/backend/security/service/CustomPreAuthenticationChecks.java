package com.m4zek.backend.security.service;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CustomPreAuthenticationChecks implements UserDetailsChecker {

    @Override
    public void check(UserDetails userDetails) {
        MyUserDetails user = (MyUserDetails) userDetails;
        if (!user.isAccountNonLocked()) {
            switch (user.getStatus()) {
                case BLOCK ->
                        throw new LockedException(
                                "Account is permanently banned."
                        );
                case SUSPENDED -> {
                    if (user.getSuspendedTo() != null) {
                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                        String formattedDate =
                                user.getSuspendedTo().format(formatter);

                        throw new LockedException(
                                "Account is suspended to: " + formattedDate
                        );
                    }
                    throw new LockedException("Account is suspended.");
                }
                default -> throw new LockedException("Account is locked.");
            }
        }

        if(!user.isEnabled()){
            throw new LockedException("Account is not activated!");
        }
    }
}
