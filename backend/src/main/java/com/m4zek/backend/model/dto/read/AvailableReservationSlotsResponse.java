package com.m4zek.backend.model.dto.read;

import com.m4zek.backend.model.projection.DayAvailability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableReservationSlotsResponse {

    private List<EmployeeSummaryResponse> employees;
    private List<DayAvailability> dayAvailabilities;

}
