package com.m4zek.backend.service.facade.admin;

import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.LoginHistory;
import com.m4zek.backend.model.RoleEnum;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserStatus;
import com.m4zek.backend.model.dto.admin.AccountListItemResponse;
import com.m4zek.backend.model.dto.admin.AccountSummaryResponse;
import com.m4zek.backend.model.projection.MonthCountProjection;
import com.m4zek.backend.service.LoginHistoryService;
import com.m4zek.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class AdminUserFacade {

    private final UserMapper userMapper;
    private final UserService userService;
    private final LoginHistoryService loginHistoryService;


    public AdminUserFacade(UserMapper userMapper, UserService userService, LoginHistoryService loginHistoryService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.loginHistoryService = loginHistoryService;
    }

    // Method to get user growth in every single month.
    // Return list of month - value ex. "January" - 3213 , "February" - 102 ....
    public List<MonthCountProjection> readUserGrowthLastYear(){

        // Read user growth from database - Like map month -> value
        Map<String, Long> chartDataMap = this.userService.getUserGrowthTrend();

        // Map to MonthCountProjection and return list of it (Sorted - last item is current month)
        return Arrays.stream(Month.values())
                .sorted(Comparator.comparingInt(m ->
                        (m.getValue() - LocalDate.now().getMonthValue() + 11) % 12))
                .map(m -> MonthCountProjection.builder()
                        .count(chartDataMap.get(m.name()))
                        .month(m.name().charAt(0) + m.name().substring(1).toLowerCase())
                        .build())
                .toList();
    }

    // Method to get users login trend - Last 12 months
    public List<MonthCountProjection> readUserLoginTrend(){

        // Read users login trend from database group by month.
        Map<String, Long> chartDataMap = this.loginHistoryService.getUserLoginTrend();

        // return sorted and added missing month (Without users login) - Sorted = current month is last
        return Arrays.stream(Month.values())
                .sorted(Comparator.comparingInt(m ->
                        (m.getValue() - LocalDate.now().getMonthValue() + 11) % 12))
                .map(m -> MonthCountProjection.builder()
                        .count(chartDataMap.get(m.name()))
                        .month(m.name().charAt(0) + m.name().substring(1).toLowerCase())
                        .build())
                .toList();
    }



    // Method to get last created accounts
    public Page<AccountSummaryResponse> readLastCreatedAccount(Pageable pageable){
        Page<User> users = this.userService.findLastCreatedAccounts(pageable);

        List<AccountSummaryResponse> accountSummaryResponses = users.stream()
                .map(this.userMapper::toAccountSummaryResponse).toList();

        return new PageImpl<>(accountSummaryResponses, pageable, users.getTotalElements());
    }


    // Method to get last account to logged in
    public Page<AccountSummaryResponse> readLastAccountLoggedIn(Pageable pageable){
        Page<LoginHistory> loginHistories = this.loginHistoryService.findLastLoggedInAccounts(pageable);

        List<AccountSummaryResponse> lastLoggedIn = loginHistories.stream()
                .map(history -> {
                            User user = this.userService.findUserById(history.getUserId());
                            return this.userMapper.toAccountSummaryResponse(history, user);
                        }
                ).toList();


        return new PageImpl<>(lastLoggedIn, pageable, loginHistories.getTotalElements());
    }

    // Method to get last logged in administrators
    public Page<AccountSummaryResponse> findLastActiveHistoryAdmins(Pageable pageable){

        List<Integer> adminIds = this.userService.findAdminIds();
        Page<LoginHistory> adminHistories = this.loginHistoryService.findLastAdminsLoggedHistory(pageable, adminIds);

        List<AccountSummaryResponse> adminsLoggedHistory = adminHistories.stream()
                        .map(history -> {
                                    User user = this.userService.findUserById(history.getUserId());
                                    return this.userMapper.toAccountSummaryResponse(history, user);
                                }
                        ).toList();

        return new PageImpl<>(adminsLoggedHistory, pageable, adminHistories.getTotalElements());
    }


    public Page<AccountListItemResponse> findAccounts(Pageable pageable, String query, List<UserStatus> statuses, List<RoleEnum> roles) {
        Page<User> accounts = this.userService.findAllUsers(pageable, query, statuses, roles);

        List<AccountListItemResponse> accountsList = accounts.stream()
                .map(this.userMapper::toAccountListItemResponse).toList();

        return new PageImpl<>(accountsList, pageable, accounts.getTotalElements());
    }
}
