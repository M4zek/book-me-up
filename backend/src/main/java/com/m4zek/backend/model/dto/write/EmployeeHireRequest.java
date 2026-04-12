package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;


@Data
public class EmployeeHireRequest {
    @NotEmpty(message = "The list of employee ids must not be empty")
    List<@Positive(message = "The id must be positive") Integer> employeeIds;
}
