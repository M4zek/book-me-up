package com.m4zek.backend.model.dto.read;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationAvailabilityResponse {
    private Date dateOfBooked;
    private List<BookedCompanyHoursResponse> bookedCompanyHours;
    private int freeTimePercentage;
}
