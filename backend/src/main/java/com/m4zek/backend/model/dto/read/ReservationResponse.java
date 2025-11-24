package com.m4zek.backend.model.dto.read;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ReservationResponse {

    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime reservationDate;
    private String reservationNumber;
    private String status;
    private UserResponse customer;
    private EmployeeSummaryResponse preferredEmployee;
    private CompanyOfferResponse companyOffer;


}
