package com.m4zek.backend.repository;

import com.m4zek.backend.model.Reservation;

import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findByUserIdAndCompanyOfferId(int userId, long companyOfferId);

    Reservation save(Reservation reservation);

}
