package com.m4zek.backend.controller;

import com.m4zek.backend.model.dto.read.ReservationAvailabilityResponse;
import com.m4zek.backend.model.dto.read.ReservationResponse;
import com.m4zek.backend.model.dto.write.ReservationAvailabilityRequest;
import com.m4zek.backend.model.dto.write.ReservationRequest;
import com.m4zek.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody ReservationAvailabilityRequest reservationAvailabilityRequest
            ) {
        return ResponseEntity.ok(this.reservationService.getCompanyReservationAvailability(reservationAvailabilityRequest));
    }


    @PostMapping("/companies/reservations")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest reservation) {
        return ResponseEntity.ok(this.reservationService.createNewReservation(reservation));
    }


}
