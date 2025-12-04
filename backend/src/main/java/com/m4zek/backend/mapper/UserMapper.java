package com.m4zek.backend.mapper;

import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import com.m4zek.backend.model.dto.read.EmployeeDetailsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {}

    public static EmployeeSummaryResponse toEmployeeSummaryResponse(User user) {
        UserData userData = user.getUserData();
        return EmployeeSummaryResponse.builder()
                .id(user.getId())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .avatar(ImageMapper.byteImageToBase64(userData.getPhoto()))
                .build();
    }

    public static EmployeeDetailsResponse toEmployeeDetailsResponse(User user, String role) {
        UserData userData = user.getUserData();
        return EmployeeDetailsResponse.builder()
                .id(user.getId())
                .email(user.getAddressEmail())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .phone(userData.getPhoneNumber())
                .role_in_company(role)
                .avatar(ImageMapper.byteImageToBase64(userData.getPhoto()))
                .build();

    }


    public static MyUserDetails toMyUserDetails(User user) {
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

    public static UserResponse toUserResponse(User user) {
        UserData userData = user.getUserData();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .email(user.getAddressEmail())
                .birthdate(userData.getDateOfBirth().toString())
                .phoneNumber(userData.getPhoneNumber())
                .avatar(userData.getPhoto())
                .build();
    }


}
