package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.UserData;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
public class UserDataWriteModel {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and optional leading +, length 7-15"
    )
    private String phoneNumber;

    @Size(max = 2_000_000, message = "Photo size cannot exceed 2MB")
    private byte[] photo;


    public UserData toEntity() {
        return new UserData(this.firstName, this.lastName, this.dateOfBirth, this.phoneNumber, this.photo);
    }

}
