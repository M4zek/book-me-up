package com.m4zek.backend.service;

import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.ReservationExistsException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.ReservationMapper;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.read.BookedCompanyHoursResponse;
import com.m4zek.backend.model.dto.read.ReservationAvailabilityResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationAvailabilityRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.repository.CompanyOfferRepository;
import com.m4zek.backend.repository.ReservationRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final CompanyOfferRepository companyOfferRepository;
    private final UserRepository userRepository;


    public ReservationService(ReservationRepository reservationRepository, CompanyOfferRepository companyOfferRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.companyOfferRepository = companyOfferRepository;
        this.userRepository = userRepository;
    }


    public List<ReservationAvailabilityResponse> getCompanyReservationAvailability(ReservationAvailabilityRequest request) {
        ZonedDateTime fromDate = request.getFromDate().atStartOfDay(ZoneId.of("Europe/Warsaw"));
        ZonedDateTime toDate = request.getToDate().atTime(23, 59, 59).atZone(ZoneId.of("Europe/Warsaw"));

        List<Reservation> reservationsCompany = this.reservationRepository.findByCompanyIdAndDateBetween(
                        request.getCompanyId(),
                        fromDate,
                        toDate);

        List<ReservationAvailabilityResponse> response = new ArrayList<>();

        if (!reservationsCompany.isEmpty()) {
            Company company = reservationsCompany.getFirst().getCompanyOffer().getCompany();

            Map<DayOfWeek, CompanyHours> hoursMap = company.getCompanyHoursList().stream()
                    .collect(Collectors.toMap(
                            h -> DayOfWeek.valueOf(h.getDayOfWeek().toUpperCase()),
                            h -> h
                    ));


            Map<LocalDate, List<Reservation>> reservationsByDate = reservationsCompany.stream()
                    .collect(Collectors.groupingBy(reservation -> reservation.getReservationDate().toLocalDate()));

            for (LocalDate date : reservationsByDate.keySet()) {

                List<Reservation> reservationsForDay = reservationsByDate.get(date);

                int totalMinutes = calculateTotalMinutes(date, hoursMap);

                if (totalMinutes == 0) {
                    response.add(ReservationAvailabilityResponse.builder()
                            .dateOfBooked(Date.from(date.atStartOfDay(ZoneId.of("Europe/Warsaw")).toInstant()))
                            .bookedCompanyHours(Collections.emptyList())
                            .freeTimePercentage(0)
                            .build());
                    continue;
                }

                List<BookedCompanyHoursResponse> bookedHours = reservationsForDay.stream()
                        .map(r -> {
                            LocalTime start = r.getReservationDate().toLocalTime();
                            LocalTime end = r.getReservationDate()
                                    .plusMinutes(r.getCompanyOffer().getDuration())
                                    .toLocalTime();

                            return BookedCompanyHoursResponse.builder()
                                    .startTimeBooked(start.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    .endTimeBooked(end.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    .build();
                        })
                        .toList();

                int bookedMinutes = bookedHours.stream().mapToInt(b ->
                        (int) ChronoUnit.MINUTES.between(
                                LocalTime.parse(b.getStartTimeBooked()),
                                LocalTime.parse(b.getEndTimeBooked())
                        )
                ).sum();


                int freeMinutesPercentage =
                        (int) (((double) (totalMinutes - bookedMinutes) / totalMinutes) * 100);

                response.add(ReservationAvailabilityResponse.builder()
                        .dateOfBooked(Date.from(date.atStartOfDay(ZoneId.of("Europe/Warsaw")).toInstant()))
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

        User preferredEmployee = this.userRepository.findById(reservationRequest.getPreferred_employee_id())
                .orElse(null);

        ZonedDateTime fromDate = reservationRequest.getReservation_date();
        ZonedDateTime toDate = reservationRequest.getReservation_date().plusMinutes(companyOffer.getDuration());

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
