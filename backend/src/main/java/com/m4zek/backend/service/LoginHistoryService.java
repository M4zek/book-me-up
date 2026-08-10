package com.m4zek.backend.service;

import com.m4zek.backend.model.LoginHistory;
import com.m4zek.backend.repository.LoginHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoginHistoryService {

    private final LoginHistoryRepository repository;


    public LoginHistoryService(LoginHistoryRepository repository) {
        this.repository = repository;
    }

    // TODO Make comments
    public Map<String, Long> getUserLoginTrend() {
        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDateTime now = LocalDateTime.now(zone);
        LocalDateTime yearAgo = now.minusYears(1).minusDays(now.getDayOfMonth() - 1);

        List<Object[]> dbResult = this.repository.getUserLoginTrendGroupByMonth(yearAgo, now);

        Map<String, Long> result = dbResult.stream()
                .collect(
                        Collectors.toMap(
                                row -> ((String) row[0]).toUpperCase(),
                                row -> (Long) row[1]
                        ));

        for(Month m: Month.values()){
            if(!result.containsKey(m.name())){
                result.put(m.name(), 0L);
            }
        }

        return result;
    }




    /**
     * Find user login history
     * @param userId - param to find all users login history by id
     * @param pageable - params to pagination results
     * @return A user history list with pagination
     */
    public Page<LoginHistory> findLoginHistoryByUserId(Integer userId, Pageable pageable){
        return this.repository.findHistoryByUserId(userId, pageable);
    }



    public Long countUsersLoggedAttemptsBetweenDate(long days, boolean success){
        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDateTime startDate = LocalDate.now(zone).atStartOfDay().minusDays(days);
        LocalDateTime endDate = LocalDate.now(zone).atTime(LocalTime.MAX);

        return repository.countLoginAttemptsBetweenDates(startDate, endDate, success);
    }


    public double getUserLoggedGrowthPercentage(int days, boolean attemptsStatus) {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime endOfPeriodA = today.atTime(LocalTime.MAX);

        LocalDateTime startOfPeriodA = today.minusDays(days).atStartOfDay();

        LocalDateTime startOfPeriodB = today.minusDays(2L * days).atStartOfDay();

        long periodA = repository.countLoginAttemptsBetweenDates(startOfPeriodA, endOfPeriodA, attemptsStatus);

        long periodB = repository.countLoginAttemptsBetweenDates(startOfPeriodB, startOfPeriodA, attemptsStatus);

        if (periodB == 0) {
            return periodA > 0 ? 100.0 : 0.0;
        }

        double growth = ((double) periodA - periodB) / periodB * 100.0;

        return Math.round(growth * 10.0) / 10.0;
    }


    public Page<LoginHistory> findLastLoggedInAccounts(Pageable pageable) {
        return this.repository.findLastLoggedInAccounts(pageable, true);
    }

    public Page<LoginHistory> findLastAdminsLoggedHistory(Pageable pageable, List<Integer> adminIds) {
        return this.repository.findLatestHistoryByUserIds(pageable, adminIds);
    }
}

