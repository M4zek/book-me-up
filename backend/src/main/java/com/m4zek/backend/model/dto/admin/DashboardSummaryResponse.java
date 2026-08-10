package com.m4zek.backend.model.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record DashboardSummaryResponse(
        MetricWithChange totalUsers,
        MetricWithChange totalCompanies,
        MetricWithChart todaysReservations,
        RevenueMetric todaysRevenue
) {
    public record MetricWithChange(
            long value,
            double changePercentage
    ) {}

    public record MetricWithChart(
            long value,
            Map<LocalDate, Long> last7Days
    ) {}

    public record RevenueMetric(
            BigDecimal value,
            Map<LocalDate, BigDecimal> last7Days
    ) {}
}
