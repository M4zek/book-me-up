package com.m4zek.backend.repository;

import com.m4zek.backend.model.Reservation;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findByUserIdAndCompanyOfferId(int userId, long companyOfferId);

    Reservation save(Reservation reservation);

    @Query("""
        SELECT r
        FROM reservations r
        JOIN r.companyOffer co
        JOIN co.company c
        WHERE c.id = :companyId
          AND r.reservationDate >= :start
          AND r.reservationDate <= :end
    """)
    List<Reservation> findByCompanyIdAndDateBetween(long companyId, ZonedDateTime start, ZonedDateTime end);

    boolean existsReservationByCompanyOffer_IdAndUserIdAndReservationDateBetween(long companyOfferId, long userId, ZonedDateTime start, ZonedDateTime end);

}
