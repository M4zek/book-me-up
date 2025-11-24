package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ReservationRequest {

    @NotNull(message = "Company offer id cannot be null")
    @Min(value = 0, message = "Company offer id must be a positive number")
    private Long company_offer_id;

    @NotNull(message = "User id cannot be null")
    @Min(value = 0, message = "User id must be a positive number")
    private int user_id;

    @Min(value = 0, message = "Employee id must be a positive number")
    private int preferred_employee_id;

    @NotNull(message = "Reservation date cannot be null")
    @Future(message = "Reservation date must be in the future")
    private OffsetDateTime reservation_date;

}
