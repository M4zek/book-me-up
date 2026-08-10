package com.m4zek.backend.mapper;

import com.m4zek.backend.model.dto.admin.DashboardSummaryResponse;
import com.m4zek.backend.model.dto.admin.UserStatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StatsMapper {


    public DashboardSummaryResponse toDashboardSummaryResponse(long totalUsersCount,
                                                               double userChange,
                                                               long totalCompaniesCount,
                                                               double companyChange,
                                                               long todayReservationsCount,
                                                               Map<LocalDate, Long> reservationsChartData,
                                                               BigDecimal todayRevenue,
                                                               Map<LocalDate, BigDecimal> revenueChart){


        DashboardSummaryResponse.MetricWithChange totalUsers =
                new DashboardSummaryResponse.MetricWithChange(totalUsersCount, userChange);

        DashboardSummaryResponse.MetricWithChange totalCompanies =
                new DashboardSummaryResponse.MetricWithChange(totalCompaniesCount, companyChange);

        DashboardSummaryResponse.MetricWithChart todaysReservations =
                new DashboardSummaryResponse.MetricWithChart(todayReservationsCount, reservationsChartData);

        DashboardSummaryResponse.RevenueMetric todaysRevenue =
                new DashboardSummaryResponse.RevenueMetric(todayRevenue, revenueChart);

        return new DashboardSummaryResponse(
                totalUsers,
                totalCompanies,
                todaysReservations,
                todaysRevenue
        );
    }


    public UserStatsResponse toUserStatsResponse(
            long totalAccountCount, double totalAccountChange,
            long todayAccountCount, double todayAccountChange,
            long loggedLastMonthCount, double loggedLastMonthChange,
            long loggedTodayCount, double loggedTodayChange
    ){
        UserStatsResponse.MetricWithChange totalAccounts =
                new UserStatsResponse.MetricWithChange(totalAccountCount, totalAccountChange);

        UserStatsResponse.MetricWithChange todayAccounts =
                new UserStatsResponse.MetricWithChange(todayAccountCount, todayAccountChange);

        UserStatsResponse.MetricWithChange loggedLastMonth =
                new UserStatsResponse.MetricWithChange(loggedLastMonthCount, loggedLastMonthChange);

        UserStatsResponse.MetricWithChange todayLogged =
                new UserStatsResponse.MetricWithChange(loggedTodayCount, loggedTodayChange);


        return new UserStatsResponse(
                totalAccounts,todayAccounts, loggedLastMonth, todayLogged
        );
    }

}
