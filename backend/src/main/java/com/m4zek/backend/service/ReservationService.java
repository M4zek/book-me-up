package com.m4zek.backend.service;

import com.m4zek.backend.exception.ReservationBadRequestException;
import com.m4zek.backend.exception.ReservationExistsException;
import com.m4zek.backend.exception.ReservationNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.write.ReservationPatchRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.model.projection.DailyCountProjection;
import com.m4zek.backend.model.projection.DailyRevenueProjection;
import com.m4zek.backend.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final static Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findCompanyReservation(int companyId, LocalDate start, LocalDate end){
        LocalDateTime fromDate = start.atStartOfDay();
        LocalDateTime toDate = end.atTime(23, 59, 59);
        return this.reservationRepository.findByCompanyIdAndDateBetween(companyId, fromDate, toDate);
    }

    // Cancel reservation by owner and reservation id
    public Reservation cancelReservation(int reservationId, User owner) {
        Reservation reservation = this.reservationRepository.findByIdAndUserId(reservationId, owner.getId())
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

        logger.info("Reservation [{}] status changed: {}", reservation.getId(), reservation.getReservationStatus());
        return reservation;
    }

    // Search reservation by user id (Results can be filtered by status and reservation offer name)
    public Page<Reservation> findReservationByUserStatusOrName(Pageable pageable, int userId, String status, String name) {
        try{
            Page<Reservation> userReservations;
            // Find with status if status exists in request
            // Else find only by user id and name
            if(status != null) {
                ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
                userReservations = this.reservationRepository.findAllByUserIdAndStatus(userId, reservationStatus , name, pageable);
            } else {
                userReservations = this.reservationRepository.findAllByUserId(userId, pageable, name);
            }

            // return reservations
            return userReservations;
        } catch (IllegalArgumentException exception){
            throw new ReservationBadRequestException("Wrong reservation status: " + status);
        }
    }


    public Reservation updateReservation(int companyId, int reservationId, ReservationPatchRequest request) {
        // If all request params  is null throw BadRequestException
        if(request.getStatus() == null && request.getPreferred_employee_id() == null){
            throw new ReservationBadRequestException("Required parameters are missing");
        }

        // Find reservation to updating
        Reservation reservation = this.reservationRepository.findByIdAndCompanyId(reservationId, companyId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));


        // Change reservation status if exists in request
        if(request.getStatus() != null && !request.getStatus().isEmpty()) {
            ReservationStatus currentStatus = ReservationStatus.from(reservation.getReservationStatus());
            ReservationStatus newStatus = ReservationStatus.from(request.getStatus());
            // If new status is not equals old status then try change it
            if(!currentStatus.equals(newStatus)){
                // Check sequences that new status can be changed if can't throw exception
                if(!currentStatus.canTransitionTo(newStatus)) {
                    throw new ReservationBadRequestException("Incorrect status change sequence: " + currentStatus + " -> " + newStatus);
                }

                logger.info("Reservation [{}] status change [{}] -> [{}]", reservation.getId(), currentStatus, newStatus);
                // Change status
                reservation.setStatus(ReservationStatus.from(request.getStatus()));
            }
        }

        // Check to change preferred employee
        User currentPrefUser = reservation.getUser();
        // If current empl id is not null and not equals new pref empl id then change pref empl
        if(request.getPreferred_employee_id() != null && currentPrefUser.getId() != request.getPreferred_employee_id()) {

            // Searching new preferred employed into company users.
            // If new preferred id not exists in company employees throw exception UserNotFund.
            User newPreferredUser = reservation.getCompanyOffer().getCompany().getUsers()
                    .stream()
                    .map(CompanyUserRole::getUser)
                    .filter(user -> request.getPreferred_employee_id() == user.getId())
                    .findAny()
                    .orElseThrow(() -> new UserNotFoundException("Employee not found in company"));

            // Assign new preferred employe to reservation
            logger.info("Change preferred Employee [{}] -> [{}] in Reservation [{}]",
                    currentPrefUser.getId(), newPreferredUser.getId(), reservation.getId());

            reservation.assignNewPreferredUser(newPreferredUser);
        }

        // Save changes
        reservation = this.reservationRepository.save(reservation);
        return reservation;
    }



    /**
     * Find company reservation between two dates ( Can be filtered by status, name or preff user)
     */
    public Page<Reservation> findCompanyReservation(
            int companyId, String name,  String status,
            Integer userId, Pageable pageable,
            LocalDate fromDate, LocalDate toDate
    ) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;

        Page<Reservation> reservationPages =
                this.reservationRepository.findAllByCompanyId(
                        companyId, name, status, userId,
                        pageable, fromDateTime, toDateTime
                );

        return reservationPages;
    }


    /**
     * Create new reservation
     * @param offer - CompanyOffer will be assigned to reservation
     * @param owner - User (Reservation owner) will be assigned to reservation
     * @param reservationRequest - Body with reservation details
     * @return Reservation - new entity
     */
    public Reservation createReservation(CompanyOffer offer, User owner, ReservationRequest reservationRequest) {

        // Check to see if the new reservation is for a day when the company is closed.
        // If so, throw an exception
        offer.getCompany().getCompanyHoursList()
                .forEach(ch -> {
                    String reservationDayName = reservationRequest.getReservation_date().getDayOfWeek().name().toUpperCase();
                    if (ch.getDayOfWeek().toUpperCase().equals(reservationDayName) && !ch.isOpen()) {
                        throw new ReservationBadRequestException("Company is closed on " + reservationDayName);
                    }
                });

        // Find preferred user into relation in company
        // If user not (pref empl) exists then throw exception "Pref empl not found"
        User preferredEmployee = offer.getCompany().getUsers().stream()
                .filter(role -> role.getUser().getId() == reservationRequest.getPreferred_employee_id())
                .findAny()
                .map(CompanyUserRole::getUser)
                .orElseThrow(() -> new UserNotFoundException("Preferred employee not found in company"));

        // Create from and to date
        LocalDateTime fromDate = reservationRequest.getReservation_date();
        LocalDateTime toDate = reservationRequest.getReservation_date()
                .plusMinutes(offer.getDuration());

        // If reservation between two dates and owner id and offer id exists
        // throw exception Reservation already exists
        if (this.reservationRepository.existsReservationByCompanyOffer_IdAndUserIdAndReservationDateBetween(
                reservationRequest.getCompany_offer_id(),
                reservationRequest.getUser_id(),
                fromDate,
                toDate
        )) {
            throw new ReservationExistsException("Reservation already exists");
        }

        // Generate reservation number
        String reservationNumber = this.createReservationNumber(
                reservationRequest.getReservation_date(),
                reservationRequest.getCompany_offer_id(),
                reservationRequest.getUser_id()
        );

        // Save new reservation into db
        Reservation savedReservation = this.reservationRepository.save(new Reservation(
                reservationRequest.getReservation_date(),
                reservationNumber,
                owner,
                offer,
                preferredEmployee
        ));

        // Return
        return savedReservation;
    }



    public long getTodayReservationCount(){
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return this.reservationRepository.countDayReservations(startOfDay, endOfDay);
    }


    public Map<LocalDate, Long> getLast7DaysReservationCountGroupByDay(){
        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDate today = LocalDate.now(zone);
        LocalDateTime sevenDaysAgo = LocalDateTime.now(zone).minusDays(6);

        Map<LocalDate, Long> dbReservationCountMap = reservationRepository.countReservationsGroupedByDay(sevenDaysAgo)
                .stream()
                .collect(Collectors.toMap(
                        DailyCountProjection::getDate,
                        DailyCountProjection::getCount
                ));

        Map<LocalDate, Long> result = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Long count = dbReservationCountMap.getOrDefault(date, 0L);
            result.put(date, count);
        }

        return result;
    }

    public BigDecimal getTodayRevenue() {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return BigDecimal.valueOf(reservationRepository.countTodayRevenue(startOfDay, endOfDay));
    }

    public Map<LocalDate, BigDecimal> getLast7DaysRevenueFromReservationsGroupByDay() {
        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDate today= LocalDate.now(zone);
        LocalDate sevenDaysAgo = LocalDate.now(zone).minusDays(6);

        LocalDateTime startPeriod = sevenDaysAgo.atStartOfDay();

        Map<LocalDate, BigDecimal> dbRevenueMap = this.reservationRepository.countRevenueGroupedByDay(startPeriod)
                .stream()
                .collect(Collectors.toMap(
                        DailyRevenueProjection::getDate,
                        DailyRevenueProjection::getRevenue
                ));

        Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal revenue = dbRevenueMap.getOrDefault(date, BigDecimal.ZERO);
            result.put(date, revenue);
        }

        return result;
    }


    /*
    ********** PRIVATE METHODS *************
    */

    // Method to generate order number / reservation number
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
}
