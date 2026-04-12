package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.ReservationStatus;
import com.m4zek.backend.model.dto.read.*;

public class ReservationMapper {

    private  ReservationMapper() {}

    public static ReservationResponse reserevationToReservationResponse(Reservation reservation) {
        EmployeeSummaryResponse preferredEmployee = reservation.getPreferredUser() != null ?
                UserMapper.toEmployeeSummaryResponse(reservation.getPreferredUser()) : null;

        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationDate(reservation.getReservationDate())
                .reservationNumber(reservation.getReservationNumber())
                .status(reservation.getReservationStatus())
                .customer(UserResponse.builder()
                        .id(reservation.getUser().getId())
                        .firstName(reservation.getUser().getUserData().getFirstName())
                        .lastName(reservation.getUser().getUserData().getLastName())
                        .phoneNumber(reservation.getUser().getUserData().getPhoneNumber())
                        .email(reservation.getUser().getAddressEmail())
                        .birthdate(reservation.getUser().getUserData().getDateOfBirth().toString())
                        .avatar(reservation.getUser().getUserData().getPhoto())
                        .build())
                .preferredEmployee(preferredEmployee)
                .companyOffer(CompanyOfferResponse.builder()
                        .id(reservation.getCompanyOffer().getId())
                        .name(reservation.getCompanyOffer().getName())
                        .description(reservation.getCompanyOffer().getDescription())
                        .price(reservation.getCompanyOffer().getPrice())
                        .duration(reservation.getCompanyOffer().getDuration())
                        .build())
                .build();
    }


    public static UserReservationResponse reservationToUserReservationResponse(Reservation reservation) {
        return UserReservationResponse.builder()
                .id(reservation.getId())
                .companyName(reservation.getCompanyOffer().getCompany().getName())
                .reservationNumber(reservation.getReservationNumber())
                .address(AddressMapper.addressToAddressResponse(reservation.getCompanyOffer().getCompany().getAddress()))
                .offer(CompanyOfferMapper.companyOfferToCompanyOfferResponse(reservation.getCompanyOffer()))
                .status(reservation.getReservationStatus())
                .reservationDate(reservation.getReservationDate())
                .companyLogo(ImageMapper.byteImageToBase64(reservation.getCompanyOffer().getCompany().getLogo()))
                .hasUserRatedOffer(
                        //If the reservation has a status other than COMPLETED, the user could not add a rating.
                        // If it is completed, it checks whether the user has added a review.
                        reservation.getReservationStatus().equals(ReservationStatus.COMPLETED.name()) &&
                                reservation.getCompanyOffer().getReviews().stream()
                                        .anyMatch(review -> review.getUser().getId() == reservation.getUser().getId())
                )
                .build();
    }


}
