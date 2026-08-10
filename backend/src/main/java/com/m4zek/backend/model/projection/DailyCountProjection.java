package com.m4zek.backend.model.projection;

import java.time.LocalDate;


public interface DailyCountProjection {
    LocalDate getDate();
    long getCount();
}
