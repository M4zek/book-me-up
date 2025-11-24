package com.m4zek.backend.model.dto.read;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationAvailabilityResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    private OffsetDateTime dateOfBooked;
    private List<BookedCompanyHoursResponse> bookedCompanyHours;
    private int freeTimePercentage;
}
