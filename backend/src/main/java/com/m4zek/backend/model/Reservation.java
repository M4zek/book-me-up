package com.m4zek.backend.model;

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

    @ManyToOne
    @JoinColumn(name = "preferred_user_id")
    private User preferredUser;

    public Reservation() {}

    public Reservation(ZonedDateTime reservationDate, String reservationNumber, User user, CompanyOffer companyOffer,  User preferredUser) {
        this.reservationDate = reservationDate;
        this.reservationNumber = reservationNumber;
        this.user = user;
        this.companyOffer = companyOffer;
        this.preferredUser = preferredUser;
        this.reservationStatus = ReservationStatus.PENDING;
    }


    public ZonedDateTime getReservationDate() {
        return this.reservationDate;
    }


    public CompanyOffer getCompanyOffer() {
        return this.companyOffer;
    }


    public int getId() {
        return id;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public String getReservationStatus() {
        return reservationStatus.name();
    }

    public User getUser() {
        return user;
    }

    public User getPreferredUser() {
        return preferredUser;
    }
}
