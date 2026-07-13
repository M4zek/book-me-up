package com.m4zek.backend.mapper;

import com.m4zek.backend.minio.MinioUrlResolver;
import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.ReservationStatus;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final MinioUrlResolver urlResolver;
    private final UserMapper userMapper;
    private final CompanyOfferMapper offerMapper;
    private final AddressMapper addressMapper;

    private  ReservationMapper(MinioUrlResolver urlResolver, UserMapper userMapper, CompanyOfferMapper offerMapper, AddressMapper addressMapper) {
        this.urlResolver = urlResolver;
        this.userMapper = userMapper;
        this.offerMapper = offerMapper;
        this.addressMapper = addressMapper;
    }

    public ReservationResponse reserevationToReservationResponse(
            Reservation reservation
    ) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationDate(reservation.getReservationDate())
                .reservationNumber(reservation.getReservationNumber())
                .status(reservation.getReservationStatus())
                .customer(this.userMapper.toUserResponse(reservation.getUser()))
                .preferredEmployee(this.userMapper.toEmployeeSummaryResponse(reservation.getPreferredUser()))
                .companyOffer(this.offerMapper.companyOfferToCompanyOfferResponse(reservation.getCompanyOffer()))
                .build();
    }


    public UserReservationResponse reservationToUserReservationResponse(Reservation reservation) {
        return UserReservationResponse.builder()
                .id(reservation.getId())
                .companyName(reservation.getCompanyOffer().getCompany().getName())
                .reservationNumber(reservation.getReservationNumber())
                .address(this.addressMapper.addressToAddressResponse(reservation.getCompanyOffer().getCompany().getAddress()))
                .offer(this.offerMapper.companyOfferToCompanyOfferResponse(reservation.getCompanyOffer()))
                .status(reservation.getReservationStatus())
                .reservationDate(reservation.getReservationDate())
                .companyLogo(this.urlResolver.imageUrlMedium(reservation.getCompanyOffer().getCompany().getLogoFile()))
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
