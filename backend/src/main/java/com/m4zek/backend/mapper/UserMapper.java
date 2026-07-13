package com.m4zek.backend.mapper;

import com.m4zek.backend.minio.MinioUrlResolver;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.EmployeeDetailsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.read.UserResponse;
import com.m4zek.backend.model.dto.read.UserToHiredResponse;
import com.m4zek.backend.model.projection.MemberProjection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final MinioUrlResolver resolver;

    private UserMapper(MinioUrlResolver resolver) {
        this.resolver = resolver;
    }

    public EmployeeSummaryResponse toEmployeeSummaryResponse(User user) {
        return EmployeeSummaryResponse.builder()
                .id(user.getId())
                .firstName(user.getUserData().getFirstName())
                .lastName(user.getUserData().getLastName())
                .avatar(this.resolver.imageUrlSmall(user.getUserData().getAvatar()))
                .build();
    }

    public EmployeeDetailsResponse toEmployeeDetailsResponse(User user, String role) {

        return EmployeeDetailsResponse.builder()
                .id(user.getId())
                .email(user.getAddressEmail())
                .firstName(user.getUserData().getFirstName())
                .lastName(user.getUserData().getLastName())
                .phone(user.getUserData().getPhoneNumber())
                .role_in_company(role)
                .avatar(this.resolver.imageUrlSmall(user.getUserData().getAvatar()))
                .build();

    }


    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getUserData().getFirstName())
                .lastName(user.getUserData().getLastName())
                .email(user.getAddressEmail())
                .birthdate(user.getUserData().getDateOfBirth().toString())
                .phoneNumber(user.getUserData().getPhoneNumber())
                .avatar_url(this.resolver.imageUrlSmall(user.getUserData().getAvatar()))
                .build();
    }


    public UserToHiredResponse userToUserToHiredResponse(User user) {
        return UserToHiredResponse.builder()
                .id(user.getId())
                .firstName(user.getUserData().getFirstName())
                .lastName(user.getUserData().getLastName())
                .companyIds(user.getCompanyUserRoles().stream()
                        .map(cur -> cur.getCompany().getId())
                        .collect(Collectors.toList())
                )
                .avatar_url(this.resolver.imageUrlSmall(user.getUserData().getAvatar()))
                .build();
    }


    public MemberProjection userToMemberProjection(User user) {
        return MemberProjection.builder()
                .id(user.getId())
                .firstName(user.getUserData().getFirstName())
                .lastName(user.getUserData().getLastName())
                .avatar(this.resolver.imageUrlSmall(user.getUserData().getAvatar()))
                .role(null)
                .build();
    }

}
