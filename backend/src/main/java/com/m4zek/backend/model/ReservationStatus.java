package com.m4zek.backend.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum ReservationStatus {
    REJECTED,
    COMPLETED,
    CANCELLED,
    ACCEPTED,
    PENDING;

    private Set<ReservationStatus> allowedTransitions;

    static {
        REJECTED.allowedTransitions = EnumSet.noneOf(ReservationStatus.class);
        COMPLETED.allowedTransitions = EnumSet.noneOf(ReservationStatus.class);
        CANCELLED.allowedTransitions = EnumSet.noneOf(ReservationStatus.class);

        ACCEPTED.allowedTransitions = EnumSet.of(COMPLETED, CANCELLED);
        PENDING.allowedTransitions = EnumSet.of(ACCEPTED, REJECTED, CANCELLED);
    }

    public boolean canTransitionTo(ReservationStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }

    public static ReservationStatus from(String value) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unknown reservation status: " + value
                        ));
    }
}
