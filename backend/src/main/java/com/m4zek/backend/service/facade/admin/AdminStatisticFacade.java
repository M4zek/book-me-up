package com.m4zek.backend.service.facade.admin;

import com.m4zek.backend.mapper.StatsMapper;
import com.m4zek.backend.model.dto.admin.DashboardSummaryResponse;
import com.m4zek.backend.model.dto.admin.UserStatsResponse;
import com.m4zek.backend.service.CompanyService;
import com.m4zek.backend.service.LoginHistoryService;
import com.m4zek.backend.service.ReservationService;
import com.m4zek.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AdminStatisticFacade {

    private final UserService userService;
    private final CompanyService companyService;
    private final ReservationService reservationService;
    private final LoginHistoryService loginHistoryService;

    private final StatsMapper statsMapper;

    public AdminStatisticFacade(UserService userService, CompanyService companyService, ReservationService reservationService, LoginHistoryService loginHistoryService, StatsMapper statsMapper) {
        this.userService = userService;
        this.companyService = companyService;
        this.reservationService = reservationService;
        this.loginHistoryService = loginHistoryService;
        this.statsMapper = statsMapper;
    }


    // Method to get main app stats
    public DashboardSummaryResponse getSystemStats(){

        // Get user statistics
        long totalUsersCount = userService.getTotalUsersCount();
        double userChange = userService.getUserGrowthPercentage(30);


        // Get Companies statistics
        long totalCompanies = companyService.getTotalCompaniesCount();
        double companyChange = companyService.getCompanyGrowthPercentage();

        // Get Reservations statistics
        long todayReservationCount = reservationService.getTodayReservationCount();
        Map<LocalDate, Long> reservationsChartData = reservationService.getLast7DaysReservationCountGroupByDay();

        // Get Revenue statistics
        BigDecimal todayRevenue = this.reservationService.getTodayRevenue();
        Map<LocalDate, BigDecimal> revenueChartData = this.reservationService.getLast7DaysRevenueFromReservationsGroupByDay();

        // Build response using mapper
        DashboardSummaryResponse response = this.statsMapper.toDashboardSummaryResponse(
                totalUsersCount, userChange,
                totalCompanies, companyChange,
                todayReservationCount, reservationsChartData,
                todayRevenue, revenueChartData
        );

        return response;
    }


    public UserStatsResponse getUsersStats() {

        // Get total account statistics
        long totalUsersCount = userService.getTotalUsersCount();
        double userChange = userService.getUserGrowthPercentage(30);

        // Get today users created stats
        long todayAccountCount = userService.countTodayCreatedUser();
        double todayAccountChange = userService.getUserGrowthPercentage(1);

        // Get users logged-in in last month
        long loggedLastMonthCount = loginHistoryService.countUsersLoggedAttemptsBetweenDate(30, true);
        double loggedLastMonthChanged = loginHistoryService.getUserLoggedGrowthPercentage(30, true);

        // Get users logged-in today
        long loggedTodayCount = loginHistoryService.countUsersLoggedAttemptsBetweenDate(1, true);
        double loggedTodayChanged = loginHistoryService.getUserLoggedGrowthPercentage(1, true);

        // Create response
        UserStatsResponse response = this.statsMapper.toUserStatsResponse(
                totalUsersCount, userChange,
                todayAccountCount, todayAccountChange,
                loggedLastMonthCount, loggedLastMonthChanged,
                loggedTodayCount, loggedTodayChanged
        );

        return response;
    }


}
