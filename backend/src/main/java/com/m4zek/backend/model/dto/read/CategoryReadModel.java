package com.m4zek.backend.model.dto.read;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryReadModel {
    private int id;
    private String name;
    private int numberOfCompanies;
}
