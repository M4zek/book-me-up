package com.m4zek.backend.model.dto.read;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedCompanyHoursResponse {


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private OffsetDateTime startTimeBooked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private OffsetDateTime endTimeBooked;

}
