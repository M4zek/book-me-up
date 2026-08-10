package com.m4zek.backend.repository;

import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.ReservationStatus;
import com.m4zek.backend.model.projection.DailyCountProjection;
import com.m4zek.backend.model.projection.DailyRevenueProjection;
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
                AND (:fromDate IS NULL OR r.reservationDate >= :fromDate)
                AND (:toDate IS NULL OR r.reservationDate <= :toDate)
        """)
    Page<Reservation> findAllByCompanyId(int companyId, String name, String status, Integer userId, Pageable pageable, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("""
        SELECT r
        FROM reservations r
        WHERE r.id = :reservationId
          AND r.companyOffer.company.id = :companyId
    """)
    Optional<Reservation> findByIdAndCompanyId(
            @Param("reservationId") int reservationId,
            @Param("companyId") int companyId);


//  ********************  STATS *****************

    @Query("""
        SELECT COUNT(r) FROM reservations r
        WHERE r.createdDate >= :from 
          AND r.createdDate <= :to
    """)
    long countDayReservations(LocalDateTime from, LocalDateTime to);

    @Query(value = """
        SELECT DATE(r.created_date) AS date, COUNT(*) AS count
        FROM reservations r
        WHERE r.created_date >= :from
        GROUP BY DATE(r.created_date)
        ORDER BY DATE(r.created_date) ASC
    """, nativeQuery = true)
    List<DailyCountProjection> countReservationsGroupedByDay(LocalDateTime from);


    @Query("""
        SELECT COALESCE(SUM(o.price), 0.0)
                FROM reservations r
                    JOIN company_offers  o ON r.companyOffer.id = o.id
                            WHERE r.createdDate >= :from
                            AND r.createdDate <= :to
        """)
    double countTodayRevenue(LocalDateTime from, LocalDateTime to);


    @Query("""
        SELECT FUNCTION('DATE', r.createdDate) AS date, COALESCE(SUM(o.price), 0.0) AS revenue
            FROM reservations r
            JOIN r.companyOffer o
            WHERE r.createdDate IS NOT NULL
              AND r.createdDate >= :from
        GROUP BY FUNCTION('DATE', r.createdDate)
    """)
    List<DailyRevenueProjection> countRevenueGroupedByDay(LocalDateTime from);

}
