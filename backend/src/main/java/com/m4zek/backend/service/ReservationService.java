package com.m4zek.backend.service;

import com.m4zek.backend.exception.*;
import com.m4zek.backend.mapper.ReservationMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.BookedCompanyHoursResponse;
import com.m4zek.backend.model.dto.read.ReservationAvailabilityResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationPatchRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReservationRepository;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final CompanyOfferRepository companyOfferRepository;
    private final UserRepository userRepository;

    @Value("${app.time-zone}")
    private String TIME_ZONE;

    public ReservationService(ReservationRepository reservationRepository, CompanyOfferRepository companyOfferRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.companyOfferRepository = companyOfferRepository;
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

    public List<ReservationAvailabilityResponse> getCompanyReservationAvailability(int companyId, LocalDate from, LocalDate to) {
        ZonedDateTime fromDate = from.atStartOfDay(ZoneId.of("Europe/Warsaw"));
        ZonedDateTime toDate = to.atTime(23, 59, 59).atZone(ZoneId.of(TIME_ZONE));

        List<Reservation> reservationsCompany = this.reservationRepository.findByCompanyIdAndDateBetween(
                companyId,
                fromDate,
                toDate
        );

        List<ReservationAvailabilityResponse> response = new ArrayList<>();

        if (!reservationsCompany.isEmpty()) {
            Company company = reservationsCompany.getFirst().getCompanyOffer().getCompany();

            Map<DayOfWeek, CompanyHours> hoursMap = company.getCompanyHoursList().stream()
                    .collect(Collectors.toMap(
                            h -> DayOfWeek.valueOf(h.getDayOfWeek().toUpperCase()),
                            h -> h
                    ));


            Map<LocalDate, List<Reservation>> reservationsByDate = reservationsCompany.stream()
                    .collect(Collectors.groupingBy(item -> item.getReservationDate().toLocalDate()));

            for (LocalDate date : reservationsByDate.keySet()) {

                List<Reservation> reservationsForDay = reservationsByDate.get(date);

                int totalMinutes = calculateTotalMinutes(date, hoursMap);

                if (totalMinutes == 0) {

                    response.add(ReservationAvailabilityResponse.builder()
                            .dateOfBooked(date.atStartOfDay(ZoneId.of(TIME_ZONE)).toOffsetDateTime())
                            .bookedCompanyHours(Collections.emptyList())
                            .freeTimePercentage(0)
                            .build());
                    continue;
                }

                List<BookedCompanyHoursResponse> bookedHours = reservationsForDay.stream()
                        .map(r -> {
                            OffsetDateTime start = r.getReservationDate().toOffsetDateTime().atZoneSameInstant(ZoneId.of(TIME_ZONE)).toOffsetDateTime();
                            OffsetDateTime end = r.getReservationDate().toOffsetDateTime().atZoneSameInstant(ZoneId.of(TIME_ZONE)).toOffsetDateTime()
                                    .plusMinutes(r.getCompanyOffer().getDuration());

                            return BookedCompanyHoursResponse.builder()
                                    .startTimeBooked(start)
                                    .endTimeBooked(end)
                                    .build();
                        })
                        .toList();

                int bookedMinutes = bookedHours.stream().mapToInt(b ->
                        (int) ChronoUnit.MINUTES.between(
                                OffsetDateTime.parse(b.getStartTimeBooked().toString()),
                                OffsetDateTime.parse(b.getEndTimeBooked().toString())
                        )
                ).sum();


                int freeMinutesPercentage =
                        (int) (((double) (totalMinutes - bookedMinutes) / totalMinutes) * 100);

                response.add(ReservationAvailabilityResponse.builder()
                        .dateOfBooked(date.atStartOfDay(ZoneId.of(TIME_ZONE)).toOffsetDateTime())
                        .bookedCompanyHours(bookedHours)
                        .freeTimePercentage(freeMinutesPercentage)
                        .build());
            }
        }

        return response;
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
                .orElse(null);

        ZonedDateTime fromDate = reservationRequest.getReservation_date().atZoneSameInstant(ZoneId.of(TIME_ZONE));
        ZonedDateTime toDate = reservationRequest.getReservation_date()
                .atZoneSameInstant(ZoneId.of(TIME_ZONE))
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
                reservationRequest.getReservation_date().atZoneSameInstant(ZoneId.of(TIME_ZONE)),
                reservationRequest.getCompany_offer_id(),
                reservationRequest.getUser_id()
        );

        return ReservationMapper.reserevationToReservationResponse(
                this.reservationRepository.save(new Reservation(
                    reservationRequest.getReservation_date().atZoneSameInstant(ZoneId.of(TIME_ZONE)),
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

    private int calculateTotalMinutes(LocalDate date, Map<DayOfWeek, CompanyHours> hoursMap) {

        DayOfWeek dow = date.getDayOfWeek();
        CompanyHours ch = hoursMap.get(dow);

        if (ch == null || !ch.isOpen()) {
            return 0;
        }

        LocalTime open = LocalTime.parse(ch.getOpenTime());
        LocalTime close = LocalTime.parse(ch.getCloseTime());

        return (int) ChronoUnit.MINUTES.between(open, close);
    }

    private String createReservationNumber(ZonedDateTime reservationDate, long companyOfferId, int userId) {
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
}
