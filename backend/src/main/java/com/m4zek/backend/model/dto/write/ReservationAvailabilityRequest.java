package com.m4zek.backend.model.dto.write;


import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationAvailabilityRequest {

    @Positive(message = "Company id must be positive number")
    Long companyId;

    @DateTimeFormat(pattern = "MMddyyyy")
    LocalDate fromDate;

    @DateTimeFormat(pattern = "MMddyyyy")
    LocalDate toDate;
}
