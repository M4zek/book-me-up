package com.m4zek.backend.model.projection;


import com.m4zek.backend.advice.ErrorMessage;
import com.m4zek.backend.exception.EmailExistsException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Data
@Builder
public class UserWriteModel {

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must be a valid email address"
    )
    private String addressEmail;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "Password must contain uppercase, lowercase, number, and special character"
    )
    private String password;

    @Valid
    private UserDataWriteModel userData;


    public User toEntity(UserData userData) {
        return new User(this.addressEmail,
                this.password,
                userData);
    }

}


//@ExceptionHandler(EmailExistsException.class)
//@ResponseStatus(HttpStatus.CONFLICT)
//public ErrorMessage handleEmailException(EmailExistsException e, WebRequest request) {
//    return new ErrorMessage(
//            HttpStatus.CONFLICT.value(),
//            new Date(),
//            e.getMessage(),
//            request.getDescription(true)
//    );
//}
//
//
//@ExceptionHandler(UserNotFoundException.class)
//@ResponseStatus(HttpStatus.NOT_FOUND)
//public ErrorMessage handleUserNotFound(UserNotFoundException e, WebRequest request) {
//    return new ErrorMessage(
//            HttpStatus.NOT_FOUND.value(),
//            new Date(),
//            e.getMessage(),
//            request.getDescription(true)
//    );
//}
