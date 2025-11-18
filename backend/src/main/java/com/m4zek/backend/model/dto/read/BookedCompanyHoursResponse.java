package com.m4zek.backend.model.dto.read;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedCompanyHoursResponse {


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String startTimeBooked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String endTimeBooked;

}
