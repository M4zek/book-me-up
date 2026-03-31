package com.m4zek.backend.controller;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.model.dto.read.AvailableReservationSlotsResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationPatchRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping(value = "/api/v1")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    // Endpoints for management company reservations
    @GetMapping("/companies/{companyId}/reservations")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_EMPLOYEE", "COMPANY_MANAGER"})
    public ResponseEntity<Page<ReservationResponse>> getCompanyReservations(
            @PathVariable @Positive(message = "Company id must be positive number") int companyId,
            @RequestParam(required = false) @Size(min = 1, message = "Offer name cannot be empty string") String name,
            @RequestParam(required = false) @Size(min = 1, message = "Status name cannot be empty string") String status,
            @RequestParam(required = false) @Positive(message = "User id must be positive number") Integer userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate toDate,
            Pageable pageable
            )
    {
        return ResponseEntity.ok(this.reservationService.getAllCompanyReservations(companyId, name, status, userId, pageable, fromDate, toDate));
    }

    @PatchMapping("/companies/{companyId}/reservations/{reservationId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
//    @HasAnyCompanyRole({"COMPANY_OWNER", "COMPANY_EMPLOYEE", "COMPANY_MANAGER"})
    public ResponseEntity<ReservationResponse> updateCompanyReservation(
            @PathVariable @Positive(message = "Company id must be positive number") Integer companyId,
            @PathVariable @Positive(message = "Reservation id must be positive number") Integer reservationId,
            @Valid @RequestBody ReservationPatchRequest request
    ){
        return ResponseEntity.ok(this.reservationService.updateReservation(companyId, reservationId, request));
    }


    // Endpoints for user reservations
    @GetMapping("/reservations/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Page<UserReservationResponse>> readAllUserReservations(
            Pageable pageable,
            @RequestParam(required = false) @Size(min = 1, message = "Offer name cannot be empty") String name,
            @RequestParam(required = false) @Size(min = 1, message = "Status can not be empty") String status,
            @PathVariable @Positive(message = "User id must be positive number") int userId) {
        return ResponseEntity.ok(this.reservationService.getUserReservations(pageable, userId, status, name));
    }

    @PatchMapping("/reservations/{id}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<UserReservationResponse> cancelReservationById(@PathVariable int id){
        return ResponseEntity.ok(this.reservationService.cancelReservation(id));
    }

    // General endpoints for reservations
    @GetMapping("/companies/{companyId}/reservations/free-slots")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AvailableReservationSlotsResponse> readReservationAvailability(
            @PathVariable @Positive(message = "Company id must be positive number") int companyId,
            @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate fromDate,
            @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate toDate,
            @RequestParam @Range(min = 10, max = 90) int duration
    ) {
        return ResponseEntity.ok(this.reservationService.generateFreeSlotsBetweenDates(companyId, fromDate, toDate, duration));
    }

    @PostMapping("/companies/reservations")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest reservation) {
        return ResponseEntity.ok(this.reservationService.createNewReservation(reservation));
    }
}
