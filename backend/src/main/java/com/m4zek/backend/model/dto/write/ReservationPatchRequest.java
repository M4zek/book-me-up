package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPatchRequest {

    @Pattern(
            regexp = "PENDING|ACCEPTED|REJECTED|COMPLETED|CANCELLED",
            message = "The status must have one of the following values: PENDING, ACCEPTED, REJECTED, COMPLETED, CANCELLED"
    )
    private String status;

    @Positive(message ="The ID of the preferred employee must be greater than 0")
    private Integer preferred_employee_id;
}


