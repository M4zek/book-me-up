package com.m4zek.backend.service.facade;


import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.mapper.ReservationMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.read.AvailableReservationSlotsResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationPatchRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationFacade {

    private final ReservationService reservationService;
    private final UserService userService;
    private final CompanyService companyService;
    private final CompanyOfferService companyOfferService;
    private final CompanyAvailabilityService availabilityService;

    private final ReservationMapper reservationMapper;


    public ReservationFacade(ReservationService reservationService,
                             UserService userService,
                             CompanyService companyService,
                             CompanyOfferService companyOfferService,
                             CompanyAvailabilityService availabilityService,
                             ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.companyService = companyService;
        this.companyOfferService = companyOfferService;
        this.availabilityService = availabilityService;
        this.reservationMapper = reservationMapper;
    }

    // Cancel reservation - Only for owner reservation
    public UserReservationResponse cancelReservation(int reservationId){
        // Get user who called the method
        User owerReservation = this.userService.findLoggedUser();
        // Cancel the reservation if possible
        Reservation reservation = this.reservationService.cancelReservation(reservationId, owerReservation);
        // Return updated reservation (Mapped in UserReservationResponse)
        return this.reservationMapper.reservationToUserReservationResponse(reservation);
    }



    // Find user reservation (Can be filtered by status or name.)
    public Page<UserReservationResponse> readUserReservations(Pageable pageable, int userId, String Status, String name){
        // Search user reservations
        Page<Reservation> reservations = this.reservationService.findReservationByUserStatusOrName(
                pageable, userId, Status, name
        );

        // Mapped reservations to UserReservationResponse
        List<UserReservationResponse> mappedReservations = reservations.stream()
                .map(this.reservationMapper::reservationToUserReservationResponse)
                .toList();

        // Return page mapped results
        return new PageImpl<>(mappedReservations, pageable, reservations.getTotalElements());
    }


    // Update reservation (Status or preferred employee or both)
    public ReservationResponse updateReservation(int companyId, int reservationId, ReservationPatchRequest request){
        // Update status or preferred employee
        Reservation reservation = this.reservationService.updateReservation(companyId, reservationId, request);

        // Return mapped updated reservation
        return this.reservationMapper.reserevationToReservationResponse(reservation);
    }


    // Method to read all company reservation between two date (from - to)
    public Page<ReservationResponse> readCompanyReservation(
            int companyId, String name, String status, Integer userId, Pageable pageable, LocalDate fromDate, LocalDate toDate){

        // Find Company Reservations
        Page<Reservation> companyReservations = this.reservationService.findCompanyReservation(
                companyId, name, status, userId, pageable, fromDate, toDate
        );


        // Mapped result values from service into ReservationResponse list.
        List<ReservationResponse> companyMappedReservations = companyReservations.stream()
                .map(this.reservationMapper::reserevationToReservationResponse)
                .toList();

        // Return page of ReservationResponse
        return new PageImpl<>(companyMappedReservations, pageable, companyReservations.getTotalElements());
    }


    // Method to create new reservation
    public ReservationResponse createReservation(ReservationRequest reservationRequest){
        // Find company offer that reservation will be assigned
        CompanyOffer offer = this.companyOfferService.findCompanyOffer(
                reservationRequest.getCompany_offer_id()
        ).orElseThrow(() -> new CompanyNotFoundException("Company offer not found"));

        // Find owner future reservation
        User owner = this.userService.findById(reservationRequest.getUser_id())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Create reservation
        Reservation reservation = this.reservationService.createReservation(
                offer, owner, reservationRequest
        );

        // Return mapped reservation
        return this.reservationMapper.reserevationToReservationResponse(reservation);
    }

    // Method to generate available reservation slots based on duration
    public AvailableReservationSlotsResponse findFreeReservationsSlots(
            int companyId, LocalDate startDate, LocalDate endDate, int duration
    ){
        // Find company base ond id or throw exception company not found
        Company company = this.companyService.findCompanyByIdOrElseThrow(companyId);

        // Read all company reservations between two date
        List<Reservation> reservations = this.reservationService.findCompanyReservation(companyId, startDate, endDate);

        // Calculate available slots to reservations
        AvailableReservationSlotsResponse availableSlots = this.availabilityService.calculateAvailability(
                company, reservations, startDate, endDate, duration
        );


        return availableSlots;
    }


}
