
// **************************************
// ********* HOME ADMIN PAGE ************
// **************************************

import {Role, UserStatus} from "../http/auth.model";

export interface MetricWithChange{
    value: number
    changePercentage: number
}

export interface MetricWithChart{
    value: number
    last7Days: Record<string, number>
}

export interface RevenueMetricWithChart{
    value: number
    last7Days: Record<string, number>
}


export interface DashboardStatistics {
    totalUsers: MetricWithChange,
    totalCompanies: MetricWithChange,
    todaysReservations: MetricWithChart,
    todaysRevenue: RevenueMetricWithChart
}

export interface UsersStatistics {
    totalAccount: MetricWithChange,
    todayAccount: MetricWithChange,
    lastMonthLogin: MetricWithChange,
    todayLogin: MetricWithChange,
}

export interface MonthCountData{
    month: string,
    count: number,
}

export interface CompanyRankingItem{
    id: number,
    avatarUrl: string | null,
    companyName: string,
    reservationCount: number,
}

export interface UserActivityItem{
    id: number,
    name: string,
    avatarUrl: string | null,
    createdAt: string | Date
}

export interface ExtendedStatus{
    value: UserStatus,
    extendedText: string | null,
}

export interface AccountListItem{
    id: number,
    name: string,
    avatarUrl: string | null,
    email: string,
    status: ExtendedStatus,
    roles: Role[]
    createdAt: string | Date
    updatedAt: string | Date
}