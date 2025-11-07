package com.m4zek.backend.service;

import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReservationRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final CompanyOfferRepository companyOfferRepository;
    private final UserRepository userRepository;


    public ReservationService(ReservationRepository reservationRepository, CompanyOfferRepository companyOfferRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.companyOfferRepository = companyOfferRepository;
        this.userRepository = userRepository;
    }



//    public ReservationResponse createNewReservation(ReservationRequest reservationRequest) {
//
//        Optional<Reservation> reservation = this.reservationRepository.findByUserIdAndCompanyOfferId(
//                reservationRequest.getUser_id(),
//                reservationRequest.getCompany_offer_id()
//        );
//
//        if (reservation.isPresent()) {
//            ZonedDateTime reservationTime = reservation.get().getReservationDate();
//            ZonedDateTime writeReservationDate = reservationRequest.getReservation_date();
//            if(reservationTime.isEqual(writeReservationDate)) {
//                throw new ReservationExistsException("Reservation already exists");
//            }
//        }
//
//        User user = this.userRepository.findById(reservationRequest.getUser_id())
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        CompanyOffer companyOffer = this.companyOfferRepository.findById(reservationRequest.getCompany_offer_id())
//                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));
//
//        String reservationNumber = this.createReservationNumber(
//                reservationRequest.getReservation_date(),
//                reservationRequest.getCompany_offer_id(),
//                reservationRequest.getUser_id()
//        );
//
//        Reservation newReservation = this.reservationRepository.save(
//                new Reservation(
//                        reservationRequest.getReservation_date(),
//                        reservationNumber,
//                        user,
//                        companyOffer
//                )
//        );
//        return newReservation.toReadModel();
//    }



    private String createReservationNumber(ZonedDateTime reservationDate, long companyOfferId, int userId) {
        String orderNumberPrefix = "ON";
        String orderCreateYear = String.valueOf(reservationDate.getYear());
        String orderCreateMonth = String.valueOf(reservationDate.getMonthValue());
        String orderCreateDay = String.valueOf(reservationDate.getDayOfMonth());
        String orderCreateHour = String.valueOf(reservationDate.getHour());
        String orderCreateMinute = String.valueOf(reservationDate.getMinute());

        return String.format("%s%s%s%s-%s%s-%d-%d",
                orderNumberPrefix,
                orderCreateYear,
                orderCreateMonth,
                orderCreateDay,
                orderCreateHour,
                orderCreateMinute,
                userId,
                companyOfferId);
    }

}
