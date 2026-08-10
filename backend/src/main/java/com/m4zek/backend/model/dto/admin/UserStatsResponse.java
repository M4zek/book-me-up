package com.m4zek.backend.model.dto.admin;

public record UserStatsResponse (
        MetricWithChange totalAccount,
        MetricWithChange todayAccount,
        MetricWithChange lastMonthLogin,
        MetricWithChange todayLogin
){
    public record MetricWithChange(
            long value,
            double changePercentage
    ) {}
}


