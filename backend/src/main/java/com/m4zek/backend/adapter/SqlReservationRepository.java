package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Reservation;
import com.m4zek.backend.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlReservationRepository extends ReservationRepository, JpaRepository<Reservation, Long> {
}
