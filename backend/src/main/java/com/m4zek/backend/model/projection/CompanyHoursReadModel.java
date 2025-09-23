package com.m4zek.backend.model.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyHoursReadModel {

    private String dayOfWeek;

    private boolean isOpen;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String openTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String closeTime;
}
