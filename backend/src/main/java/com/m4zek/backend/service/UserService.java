package com.m4zek.backend.service;

import com.m4zek.backend.exception.EmailExistsException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import com.m4zek.backend.model.projection.UserWriteModel;
import com.m4zek.backend.repository.UserDateRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDateRepository userDateRepository;
    private final UserRepository userRepository;

    public UserService(UserDateRepository userDateRepository, UserRepository userRepository) {
        this.userDateRepository = userDateRepository;
        this.userRepository = userRepository;
    }

    /*
    Create new user method.
    TODO It should be reworked when the Spring Security dependency is added
     */
    public User createUser(UserWriteModel userWriteModel) {

        String userEmail = userWriteModel.getAddressEmail();

        if(this.userRepository.existsByAddressEmail(userEmail)) {
            throw new EmailExistsException("Address email already exists");
        }

        UserData userData = userWriteModel.getUserData().toEntity();
        userData = this.userDateRepository.save(userData);
        User userToCreate = userWriteModel.toEntity(userData);
        return this.userRepository.save(userToCreate);
    }


    private User findUserByEmail(String email) {
        return this.userRepository.findByAddressEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email [" + email + "] not found")
        );
    }



}
