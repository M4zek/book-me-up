package com.m4zek.backend.repository;

import com.m4zek.backend.model.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository {

    LoginHistory save(LoginHistory loginHistory);

    @Query("""
       SELECT
            DATE_FORMAT(h.createdDate, '%M'),
            COUNT(DISTINCT(h.userId))
            FROM login_history h
                 WHERE h.createdDate >= :from
                     AND h.success = true
            GROUP BY MONTH(h.createdDate)
            ORDER BY MONTH(h.createdDate)
    """)
    List<Object[]> getUserLoginTrendGroupByMonth(LocalDateTime from, LocalDateTime to);


    @Query("""
        SELECT h FROM login_history h
            WHERE h.userId = :userId
    """)
    Page<LoginHistory> findHistoryByUserId(Integer userId, Pageable pageable);


    @Query("""
           SELECT COUNT(DISTINCT(h.userId)) FROM login_history h
               WHERE h.createdDate >= :startDate
                   AND h.createdDate < :endDate
                   AND h.success = :success
    """)
    long countLoginAttemptsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("success") Boolean success);

    @Query("""
        SELECT h
        FROM login_history h
        WHERE h.success = :success
          AND h.userId IS NOT NULL
          AND h.createdDate = (
              SELECT MAX(h2.createdDate)
              FROM login_history h2
              WHERE h2.userId = h.userId
                AND h2.success = :success
          )
        ORDER BY h.createdDate DESC
    """)
    Page<LoginHistory> findLastLoggedInAccounts(Pageable pageable, Boolean success);


    @Query("""
        SELECT lh
        FROM login_history lh
        WHERE lh.userId IS NOT NULL
          AND lh.userId IN :userIds
          AND lh.createdDate = (
              SELECT MAX(lh2.createdDate)
              FROM login_history lh2
              WHERE lh2.userId = lh.userId
          )
    """)
    Page<LoginHistory> findLatestHistoryByUserIds(Pageable pageable,
                                                  @Param("userIds") List<Integer> userIds);
}
