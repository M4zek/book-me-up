package com.m4zek.backend.service;

import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.dto.read.AvailableReservationSlotsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.projection.AvailableSlots;
import com.m4zek.backend.model.projection.DayAvailability;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyAvailabilityService {

    private final UserMapper userMapper;

    public CompanyAvailabilityService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public AvailableReservationSlotsResponse calculateAvailability(
            Company company,
            List<Reservation> reservations,
            LocalDate startDate,
            LocalDate endDate,
            int duration
    ) {

        List<EmployeeSummaryResponse> employees =
                mapEmployees(company);

        Map<LocalDate, List<Reservation>> reservationsByDate =
                groupReservationsByDate(reservations);

        List<DayAvailability> days =
                buildDays(
                        company,
                        employees,
                        reservationsByDate,
                        startDate,
                        endDate,
                        duration
                );

        return AvailableReservationSlotsResponse.builder()
                .employees(employees)
                .dayAvailabilities(days)
                .build();
    }

    // ----------------------------
    // EMPLOYEES
    // ----------------------------

    private List<EmployeeSummaryResponse> mapEmployees(Company company) {
        return company.getUsers().stream()
                .map(rel -> userMapper.toEmployeeSummaryResponse(rel.getUser()))
                .toList();
    }

    // ----------------------------
    // RESERVATIONS GROUPING
    // ----------------------------

    private Map<LocalDate, List<Reservation>> groupReservationsByDate(
            List<Reservation> reservations
    ) {
        return reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReservationDate().toLocalDate()
                ));
    }

    // ----------------------------
    // MAIN DAY BUILDING
    // ----------------------------

    private List<DayAvailability> buildDays(
            Company company,
            List<EmployeeSummaryResponse> employees,
            Map<LocalDate, List<Reservation>> reservationsByDate,
            LocalDate startDate,
            LocalDate endDate,
            int duration
    ) {

        List<DayAvailability> result = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            CompanyHours hours =
                    findHours(company, current);

            List<Reservation> dayReservations =
                    reservationsByDate.getOrDefault(
                            current,
                            Collections.emptyList()
                    );

            DayAvailability day =
                    buildDay(
                            company,
                            current,
                            hours,
                            employees,
                            dayReservations,
                            duration
                    );

            result.add(day);

            current = current.plusDays(1);
        }

        return result;
    }

    // ----------------------------
    // SINGLE DAY
    // ----------------------------

    private DayAvailability buildDay(
            Company company,
            LocalDate day,
            CompanyHours hours,
            List<EmployeeSummaryResponse> employees,
            List<Reservation> reservations,
            int duration
    ) {

        if (isClosed(day, hours)) {
            return DayAvailability.builder()
                    .date(day.atStartOfDay())
                    .isOpen(false)
                    .build();
        }

        List<AvailableSlots> slots =
                generateSlots(
                        day.atStartOfDay(),
                        hours.getOpenTime(),
                        hours.getCloseTime(),
                        duration
                );

        assignEmployeesToSlots(
                slots,
                employees,
                reservations
        );

        int freePercentage =
                calculateFreeTimePercentage(
                        hours,
                        employees.size(),
                        reservations
                );

        return DayAvailability.builder()
                .date(day.atStartOfDay())
                .isOpen(true)
                .slots(slots)
                .freeTimePercentage(freePercentage)
                .build();
    }

    // ----------------------------
    // BUSINESS RULES
    // ----------------------------

    private boolean isClosed(LocalDate day, CompanyHours hours) {

        if (hours == null || !hours.isOpen()) {
            return true;
        }

        if (day.isBefore(LocalDate.now())) {
            return true;
        }

        return day.isEqual(LocalDate.now())
                && LocalTime.now().isAfter(
                LocalTime.parse(hours.getCloseTime())
        );
    }

    private CompanyHours findHours(Company company, LocalDate day) {

        String dayName = day.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return company.getCompanyHoursList().stream()
                .filter(h -> h.getDayOfWeek().equalsIgnoreCase(dayName))
                .findFirst()
                .orElse(null);
    }

    // ----------------------------
    // SLOTS + EMPLOYEES
    // ----------------------------

    private void assignEmployeesToSlots(
            List<AvailableSlots> slots,
            List<EmployeeSummaryResponse> employees,
            List<Reservation> reservations
    ) {

        for (AvailableSlots slot : slots) {

            Set<Integer> busy =
                    findBusyEmployees(slot, reservations);

            List<Integer> available =
                    employees.stream()
                            .map(EmployeeSummaryResponse::getId)
                            .filter(id -> !busy.contains(id))
                            .toList();

            slot.setAvailableEmployeeIds(new ArrayList<>(available));
        }
    }

    private Set<Integer> findBusyEmployees(
            AvailableSlots slot,
            List<Reservation> reservations
    ) {

        LocalTime slotStart = LocalTime.parse(slot.getStart());
        LocalTime slotEnd = LocalTime.parse(slot.getEnd());

        return reservations.stream()
                .filter(r -> {
                    LocalTime reservationStart = r.getReservationDate().toLocalTime();
                    LocalTime reservationEnd = r.getReservationDate().toLocalTime().plusMinutes(r.getCompanyOffer().getDuration());

                    return slotStart.isBefore(reservationEnd)
                            && slotEnd.isAfter(reservationStart);
                })
                .map(r -> r.getPreferredUser().getId())
                .collect(Collectors.toSet());
    }

    // ----------------------------
    // PERCENTAGE
    // ----------------------------

    private int calculateFreeTimePercentage(
            CompanyHours hours,
            int employeesCount,
            List<Reservation> reservations
    ) {

        int totalMinutes =
                calculateTotalMinutes(hours) * employeesCount;

        int bookedMinutes =
                reservations.stream()
                        .mapToInt(r -> r.getCompanyOffer().getDuration())
                        .sum();

        if (totalMinutes == 0) {
            return 0;
        }

        return (int) (((double) (totalMinutes - bookedMinutes)
                / totalMinutes) * 100);
    }

    // ----------------------------
    // LOGIC
    // ----------------------------

    private int calculateTotalMinutes(CompanyHours hours) {

        if (hours == null || !hours.isOpen()) {
            return 0;
        }

        LocalTime open = LocalTime.parse(hours.getOpenTime());
        LocalTime close = LocalTime.parse(hours.getCloseTime());

        return (int) ChronoUnit.MINUTES.between(open, close);
    }

    private List<AvailableSlots> generateSlots(
            LocalDateTime date,
            String startTime,
            String endTime,
            int duration
    ) {

        LocalDate today = LocalDate.now();
        LocalDate day = date.toLocalDate();

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        if (today.isEqual(day) && LocalTime.now().isAfter(start)) {
            start = roundTime(LocalTime.now());
        }

        List<AvailableSlots> slots = new ArrayList<>();
        LocalTime endSlot = start.plusMinutes(duration);

        while (!endSlot.isAfter(end)) {

            slots.add(
                    AvailableSlots.builder()
                            .start(start.format(DateTimeFormatter.ofPattern("HH:mm")))
                            .end(endSlot.format(DateTimeFormatter.ofPattern("HH:mm")))
                            .build()
            );

            start = endSlot;
            endSlot = endSlot.plusMinutes(duration);
        }

        return slots;
    }

    private LocalTime roundTime(LocalTime time) {
        int minutes = time.getMinute();
        int rounded = (minutes / 10) * 10;
        return time.withMinute(rounded).withSecond(0).withNano(0);
    }
}
