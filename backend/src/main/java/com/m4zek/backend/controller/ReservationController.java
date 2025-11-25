package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.ReservationAvailabilityResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.read.UserReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/companies/{companyId}/reservations")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<ReservationAvailabilityResponse>> readReservationAvailability(
            @PathVariable @Positive(message = "Company id must be positive number") int companyId,
            @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate fromDate,
            @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate toDate
    ) {
        return ResponseEntity.ok(this.reservationService.getCompanyReservationAvailability(companyId, fromDate, toDate));
    }


    @PostMapping("/companies/reservations")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest reservation) {
        return ResponseEntity.ok(this.reservationService.createNewReservation(reservation));
    }


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

}
