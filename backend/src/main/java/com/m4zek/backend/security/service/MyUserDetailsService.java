package com.m4zek.backend.security.service;

import com.m4zek.backend.model.User;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository usersRepository;


    public MyUserDetailsService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usersRepository.findByAddressEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with given username not found"));

        return build(user);
    }

    private MyUserDetails build(User user){
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new MyUserDetails(
                user.getId(),
                user.getAddressEmail(),
                user.getPassword(),
                user.getBlock(),
                user.getEnable(),
                user.getUserData().getId(),
                grantedAuthorities
        );
    }
}
