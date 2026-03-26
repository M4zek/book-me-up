package com.m4zek.backend.service;

import com.m4zek.backend.exception.*;
import com.m4zek.backend.mapper.ReservationMapper;
import com.m4zek.backend.mapper.UserMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.AvailableReservationSlotsResponse;
import com.m4zek.backend.model.dto.read.EmployeeSummaryResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationPatchRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.model.projection.AvailableSlots;
import com.m4zek.backend.model.projection.DayAvailability;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.CompanyRepository;
import com.m4zek.backend.repository.ReservationRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final CompanyOfferRepository companyOfferRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    public ReservationService(ReservationRepository reservationRepository, CompanyOfferRepository companyOfferRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.companyOfferRepository = companyOfferRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Page<UserReservationResponse> getUserReservations(Pageable pageable, int userId, String status, String name) {
        try{
            Page<Reservation> userReservations = null;
            if(status != null) {
                ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
                userReservations = this.reservationRepository.findAllByUserIdAndStatus(userId, reservationStatus , name, pageable);
            } else {
                userReservations = this.reservationRepository.findAllByUserId(userId, pageable, name);
            }

            List<UserReservationResponse> userReservationResponses = userReservations.stream()
                    .map(ReservationMapper::reservationToUserReservationResponse).toList();

            return new PageImpl<>(userReservationResponses, pageable, userReservations.getTotalElements());
        } catch (IllegalArgumentException exception){
            throw new ReservationBadRequestException("Wrong reservation status: " + status);
        }
    }

    public UserReservationResponse cancelReservation(int reservationId) {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loggedUserId = myUserDetails.getId();

        Reservation reservation = this.reservationRepository.findByIdAndUserId(reservationId, loggedUserId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));

        if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED.name())) {
            throw new ReservationBadRequestException("Completed reservation cannot be cancelled");
        } else if (reservation.getReservationStatus().equals(ReservationStatus.CANCELLED.name())) {
            throw new ReservationBadRequestException("Reservation is already cancelled");
        } else if (reservation.getReservationStatus().equals(ReservationStatus.REJECTED.name())) {
            throw new ReservationBadRequestException("Reservation is already rejected");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = this.reservationRepository.save(reservation);
        return ReservationMapper.reservationToUserReservationResponse(reservation);
    }


    public AvailableReservationSlotsResponse generateFreeSlotsBetweenDates(int companyId, LocalDate startDate, LocalDate endDate, int duration) {
        LocalDateTime fromDate = startDate.atStartOfDay();
        LocalDateTime toDate = endDate.atTime(23, 59, 59);

        // Find all reservations in company between two dates
        List<Reservation> reservations = this.reservationRepository.findByCompanyIdAndDateBetween(
                companyId,
                fromDate,
                toDate
        );


        Map<LocalDate, List<Reservation>> reservationsByDate = reservations.stream()
                .collect(Collectors.groupingBy(item -> item.getReservationDate().toLocalDate()));



        // Empty list for day data
        List<DayAvailability> dayAvailabilities = new ArrayList<>();

        // Get all company employees
        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));

        List<EmployeeSummaryResponse> employees = company.getUsers().stream()
                .map(roles -> UserMapper.toEmployeeSummaryResponse(roles.getUser()))
                .toList();

        // List of company hours (week)
        List<CompanyHours> companyHours = company.getCompanyHoursList();

        // Generate days (from -> to)
        List<LocalDateTime> week = new ArrayList<>();
        while (fromDate.isBefore(toDate)) {
            week.add(fromDate);
            fromDate = fromDate.plusDays(1);
        }

        // Map |  day -> companyHours
        Map<LocalDateTime, CompanyHours> companyHoursMap =
                companyHours.stream().collect(Collectors.toMap(
                        h -> {
                            LocalDateTime date = week.stream()
                                    .filter(w -> w.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                    .equalsIgnoreCase(h.getDayOfWeek())).findFirst().get();

                            return date;
                        },
                        h -> h,
                        (a,b) -> a, TreeMap::new
                ));

        // Generate response
        companyHoursMap.forEach((day, hours) -> {

            if(!hours.isOpen()) {
                dayAvailabilities.add(
                        DayAvailability.builder()
                                .date(day)
                                .isOpen(false)
                                .build()
                );
                return;
            }

            // Create slots based on opening hours.
            List<AvailableSlots> slots = generateSlots(hours.getOpenTime(), hours.getCloseTime(), duration);

            // Create date e.g: (2025-10-20)
            LocalDate localDay = day.toLocalDate();

            // Get reservations by day
            List<Reservation> reservationAtDay = reservationsByDate.get(localDay) != null ? reservationsByDate.get(localDay) : new ArrayList<>();

            // If company don't have reservation on this day, all employees are available
            // Otherwise check which employees have reservations at this slot time and delete their from available employees
            if(reservationAtDay.isEmpty()){
                List<Integer> availableEmployeeIds = new ArrayList<>(employees.stream().map(EmployeeSummaryResponse::getId)
                        .toList());

                slots.forEach(slot -> {
                    slot.setAvailableEmployeeIds(availableEmployeeIds);
                });
            } else {
                // Adding available employees at this time for each free time slots
                slots.forEach(slot -> {

                    // Filter employees who have reservations at this time (slot)
                    Set<Integer> busyEmployeeIds = reservations.stream()
                            .filter(res-> {
                                LocalTime startReservation = res.getReservationDate().toLocalTime();
                                LocalTime startSlot = LocalTime.parse(slot.getStart());
                                LocalTime endSlot = LocalTime.parse(slot.getEnd());

                                return startReservation.isBefore(endSlot) && startReservation.isAfter(startSlot);
                            }).map(res-> res.getPreferredUser().getId())
                            .collect(Collectors.toSet());

                    // Remove busy employee ids = Available employees (ids) at this time
                    List<Integer> availableEmployeeIds = new ArrayList<>(employees.stream()
                            .map(EmployeeSummaryResponse::getId)
                            .filter(id -> !busyEmployeeIds.contains(id))
                            .toList());

                    slot.setAvailableEmployeeIds(availableEmployeeIds);
                });
            }

            // Calculating free time (minutes) in percentage in a day
            int totalMinutes = calculateTotalMinutes(hours) * employees.size();
            int bookedMinutes = reservationAtDay.stream()
                        .mapToInt(res -> res.getCompanyOffer().getDuration()).sum();

            int percentageFreeSlots = (int) (((double) (totalMinutes - bookedMinutes) / totalMinutes) * 100);

            // Add to the list a data about day
            dayAvailabilities.add(
                    DayAvailability.builder()
                            .date(day)
                            .slots(slots)
                            .isOpen(true)
                            .freeTimePercentage(percentageFreeSlots)
                            .build()
            );
        });


        return AvailableReservationSlotsResponse.builder()
                .employees(employees)
                .dayAvailabilities(dayAvailabilities)
                .build();
    }

    public ReservationResponse createNewReservation(ReservationRequest reservationRequest) {
        CompanyOffer companyOffer = this.companyOfferRepository.findById(reservationRequest.getCompany_offer_id())
                .orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));

        User user = this.userRepository.findById(reservationRequest.getUser_id())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        companyOffer.getCompany().getCompanyHoursList()
                .forEach(ch -> {
                    String reservationDayName = reservationRequest.getReservation_date().getDayOfWeek().name().toUpperCase();
                    if (ch.getDayOfWeek().toUpperCase().equals(reservationDayName) && !ch.isOpen()) {
                        throw new ReservationBadRequestException("Company is closed on " + reservationDayName);
                    }
                });

        User preferredEmployee = this.userRepository.findById(reservationRequest.getPreferred_employee_id())
                .orElseThrow(() -> new UserNotFoundException("Preferred employee not found"));

        LocalDateTime fromDate = reservationRequest.getReservation_date();
        LocalDateTime toDate = reservationRequest.getReservation_date()
                .plusMinutes(companyOffer.getDuration());

        if (this.reservationRepository.existsReservationByCompanyOffer_IdAndUserIdAndReservationDateBetween(
                reservationRequest.getCompany_offer_id(),
                reservationRequest.getUser_id(),
                fromDate,
                toDate
        )) {
            throw new ReservationExistsException("Reservation already exists");
        }

        String reservationNumber = this.createReservationNumber(
                reservationRequest.getReservation_date(),
                reservationRequest.getCompany_offer_id(),
                reservationRequest.getUser_id()
        );

        return ReservationMapper.reserevationToReservationResponse(
                this.reservationRepository.save(new Reservation(
                    reservationRequest.getReservation_date(),
                    reservationNumber,
                    user,
                    companyOffer,
                    preferredEmployee
            ))
        );
    }

    /*
        Method to update reservation
     */
    public ReservationResponse updateReservation(int companyId, int reservationId, ReservationPatchRequest request) {
        Reservation reservation = this.reservationRepository.findByIdAndCompanyId(reservationId, companyId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        ReservationStatus currentStatus = ReservationStatus.from(reservation.getReservationStatus());
        ReservationStatus newStatus = ReservationStatus.from(request.getStatus());

        if(!request.getStatus().isEmpty() && !currentStatus.equals(newStatus)) {

            if(!currentStatus.canTransitionTo(newStatus)) {
                throw new ReservationBadRequestException("Incorrect status change sequence: " + currentStatus + " -> " + newStatus);
            }

            reservation.setStatus(ReservationStatus.from(request.getStatus()));
        }


        User prefUser = reservation.getUser();

        if(prefUser == null || prefUser.getId() != request.getPreferred_employee_id()) {

            User newPreferredUser = reservation.getCompanyOffer().getCompany().getUsers()
                    .stream()
                    .map(CompanyUserRole::getUser)
                    .filter(user -> request.getPreferred_employee_id() == user.getId())
                    .findAny()
                    .orElseThrow(() -> new UserNotFoundException("Employee not found in company"));

            reservation.assignNewPreferredUser(newPreferredUser);
        }

        reservation = this.reservationRepository.save(reservation);
        return ReservationMapper.reserevationToReservationResponse(reservation);
    }

    public Page<ReservationResponse> getAllCompanyReservations(int companyId, String name,  String status, Integer userId, Pageable pageable) {
        Page<Reservation> reservationPages = this.reservationRepository.findAllByCompanyId(companyId, name, status, userId, pageable);

        List<ReservationResponse> reservationList = reservationPages.stream()
                .map(ReservationMapper::reserevationToReservationResponse)
                .toList();

        return new PageImpl<>(reservationList, pageable, reservationPages.getTotalElements());
    }

    /*
        PRIVATE METHODS
     */

    private int calculateTotalMinutes(CompanyHours hoursMap) {
        if (hoursMap == null || !hoursMap.isOpen()) {
            return 0;
        }

        LocalTime open = LocalTime.parse(hoursMap.getOpenTime());
        LocalTime close = LocalTime.parse(hoursMap.getCloseTime());

        return (int) ChronoUnit.MINUTES.between(open, close);
    }

    private String createReservationNumber(LocalDateTime reservationDate, long companyOfferId, int userId) {
        String orderNumberPrefix = "ON";
        String orderCreateYear = String.valueOf(reservationDate.getYear());
        String orderCreateMonth = String.valueOf(reservationDate.getMonthValue());
        String orderCreateDay = String.valueOf(reservationDate.getDayOfMonth());
        String orderCreateHour = String.valueOf(reservationDate.getHour());
        String orderCreateMinute = String.valueOf(reservationDate.getMinute());
        String orderCreateSecond = String.valueOf(LocalDateTime.now().getSecond());

        return String.format("%s%s%s%s-%s%s%s-%d-%d",
                orderNumberPrefix,
                orderCreateYear,
                orderCreateMonth,
                orderCreateDay,
                orderCreateHour,
                orderCreateMinute,
                orderCreateSecond,
                userId,
                companyOfferId);
    }

    // Method generate free time slots
    private List<AvailableSlots> generateSlots(String startTime, String endTime, int duration) {

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        List<AvailableSlots> availableSlots = new ArrayList<>();
        LocalTime endTimeSlot = start.plusMinutes(duration);

        while (!endTimeSlot.isAfter(end)) {

            availableSlots.add(
                    AvailableSlots.builder()
                            .start(start.format(DateTimeFormatter.ofPattern("HH:mm")))
                            .end(endTimeSlot.format(DateTimeFormatter.ofPattern("HH:mm")))
                            .build()
            );

            start = endTimeSlot;
            endTimeSlot = endTimeSlot.plusMinutes(duration);
        }

        return availableSlots;
    }
}
