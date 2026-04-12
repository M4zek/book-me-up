package com.m4zek.backend.model.dto.read;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReservationResponse {

    private int id;
    private String companyName;
    private String reservationNumber;
    private AddressResponse address;
    private CompanyOfferResponse offer;
    private String status;
    private LocalDateTime reservationDate;
    private String companyLogo;
    private boolean hasUserRatedOffer;
}
