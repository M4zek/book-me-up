package com.m4zek.backend.controller;

import com.m4zek.backend.service.ReservationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company-reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
// TODO REBUILD ALL RESERVATION WITH SERVICE

//    @PostMapping
//    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest reservation) {
//        return ResponseEntity.ok(this.reservationService.createNewReservation(reservation));
//    }


}
