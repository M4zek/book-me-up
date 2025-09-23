package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

@Data
@Builder
public class ReservationReadModel {

    private int id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime reservationDate;
    private String reservationNumber;
    private String status;
    private UserReadModel customer;
    private CompanyOfferReadModel companyOffer;


}
