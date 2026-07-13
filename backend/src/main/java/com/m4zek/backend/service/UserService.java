package com.m4zek.backend.service;

import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.Role;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }


    public User createAndSaveUser(UserRequest request){
        Role userRole = this.roleService.findRoleUser()
                .orElseThrow(() -> new UserNotFoundException("User role not found"));

        Set<Role> roles = Set.of(userRole);

        UserData userData = new UserData(
                request.getUserData().getFirstName(),
                request.getUserData().getLastName(),
                request.getUserData().getDateOfBirth(),
                request.getUserData().getPhoneNumber()
        );

        User user = new User(
                request.getAddressEmail(),
                passwordEncoder.encode(request.getPassword()),
                userData,
                roles
        );

        return this.save(user);
    }

    public User save(User newUser){
        User user = this.userRepository.save(newUser);
        logger.info("User has been saved [{}]", user.getAddressEmail());
        return user;
    }

    public Optional<User> findById(int id){
        return this.userRepository.findById(id);
    }

    public User findUserById(int id){
        return this.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with given id not found"));
    }

    public List<User> findAllByIds(List<Integer> ids){
        return this.userRepository.findAllByIdIn(ids);
    }

    public boolean userExistsByEmail(String email){
        return this.userRepository.existsByAddressEmail(email);
    }

    public User findLoggedUser(){
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.findUserByEmail(myUserDetails.getEmail());
    }

    public User findUserByEmail(String email) {
        return this.userRepository.findByAddressEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email [" + email + "] not found")
        );
    }



    public Page<User> findUsersByNameAndSurname(Pageable pageable, String firstName, String lastName){
        return this.userRepository.findAllByFirstNameAndLastname(pageable, firstName, lastName);
    }


}
