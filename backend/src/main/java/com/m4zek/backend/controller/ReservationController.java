package com.m4zek.backend.controller;

import com.m4zek.backend.model.projection.ReservationReadModel;
import com.m4zek.backend.model.projection.ReservationWriteModel;
import com.m4zek.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company-reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationReadModel> createReservation(@RequestBody @Valid ReservationWriteModel reservation) {
        return ResponseEntity.ok(this.reservationService.createNewReservation(reservation));
    }


}
