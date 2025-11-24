package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.ReservationAvailabilityResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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


}
