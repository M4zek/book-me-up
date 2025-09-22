package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.ReservationReadModel;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity(name = "reservations")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private ZonedDateTime reservationDate;

    private String reservationNumber;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_offer_id")
    private CompanyOffer companyOffer;

    public Reservation() {}

    public Reservation(ZonedDateTime reservationDate, String reservationNumber, User user, CompanyOffer companyOffer) {
        this.reservationDate = reservationDate;
        this.reservationNumber = reservationNumber;
        this.user = user;
        this.companyOffer = companyOffer;
        this.reservationStatus = ReservationStatus.PENDING;
    }


    public ZonedDateTime getReservationDate() {
        return this.reservationDate;
    }

    public ReservationReadModel toReadModel() {
        return ReservationReadModel.builder()
                .customer(this.user.toUserReadModel())
                .status(this.reservationStatus.toString())
                .companyOffer(this.companyOffer.toReadModel())
                .reservationNumber(this.reservationNumber)
                .reservationDate(this.reservationDate)
                .build();
    }

}
