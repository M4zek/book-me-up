package com.m4zek.backend.repository;

import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findByIdAndUserId(int id, int userId);

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
    List<Reservation> findByCompanyIdAndDateBetween(long companyId, LocalDateTime start, LocalDateTime end);

    boolean existsReservationByCompanyOffer_IdAndUserIdAndReservationDateBetween(long companyOfferId, long userId, LocalDateTime start, LocalDateTime end);


    @Query("""
        SELECT r from reservations r
            WHERE (r.user.id = :userId)
                AND (:name is NULL or LOWER(r.companyOffer.name) LIKE LOWER(CONCAT('%', :name, '%')))
        """)
    Page<Reservation> findAllByUserId(@Param("userId") long userId, Pageable pageable, @Param("name") String name);


    @Query("""
        SELECT r from reservations r
            WHERE (r.user.id = :userId)
                AND(r.status = :status)
                AND (:name is NULL or LOWER(r.companyOffer.name) LIKE LOWER(CONCAT('%', :name, '%')))
        """)
    Page<Reservation> findAllByUserIdAndStatus(@Param("userId") long userId,
                                               @Param("status") ReservationStatus status,
                                               @Param("name")  String name,
                                               Pageable pageable);


    @Query("""
        SELECT r from reservations r
                WHERE (r.companyOffer.company.id = :companyId)
                AND (:name IS NULL OR LOWER(r.companyOffer.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:status IS NULL OR LOWER(r.status) LIKE LOWER(CONCAT('%', :status, '%')))
                AND (:userId IS NULL OR r.preferredUser.id = :userId)
        """)
    Page<Reservation> findAllByCompanyId(int companyId, String name, String status, Integer userId, Pageable pageable);

    @Query("""
        SELECT r
        FROM reservations r
        WHERE r.id = :reservationId
          AND r.companyOffer.company.id = :companyId
    """)
    Optional<Reservation> findByIdAndCompanyId(
            @Param("reservationId") int reservationId,
            @Param("companyId") int companyId);
}
