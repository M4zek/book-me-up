package com.m4zek.backend.model.projection;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableSlots {
    private String start;
    private String end;
    private List<Integer> availableEmployeeIds;
}
