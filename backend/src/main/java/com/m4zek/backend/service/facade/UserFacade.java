package com.m4zek.backend.service.facade;

import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.read.UserToHiredResponse;
import com.m4zek.backend.model.projection.MemberProjection;
import com.m4zek.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFacade {
    private final static Logger logger = LoggerFactory.getLogger(UserFacade.class);

    private final UserMapper userMapper;
    private final UserService userService;

    public UserFacade(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }



    // Find logged user and return user details
    public UserResponse getLoggedUserDetails(){
        // Find logged user
        User user = this.userService.findLoggedUser();

        // Map user to user response
        UserResponse loggedUser = this.userMapper.toUserResponse(user);

        logger.info("Read logged user details [{}]", loggedUser.getEmail());
        return loggedUser;
    }


    public Page<UserToHiredResponse> searchUsersToHireIntoCompany(Pageable pageable, String firstName, String lastName){
        Page<User> users = this.userService.findUsersByNameAndSurname(pageable, firstName, lastName);

        List<UserToHiredResponse> members = users.stream()
                .map(this.userMapper::userToUserToHiredResponse)
                .toList();

        return new PageImpl<>(members, pageable, users.getTotalElements());
    }


    public Page<MemberProjection> searchUsers(Pageable pageable, String firstName, String lastName){
        Page<User> users = this.userService.findUsersByNameAndSurname(pageable, firstName, lastName);

        List<MemberProjection> members = users.stream()
                .map(this.userMapper::userToMemberProjection)
                .toList();

        return new PageImpl<>(members, pageable, users.getTotalElements());
    }


}
