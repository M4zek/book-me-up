package com.m4zek.backend.model.dto.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyReadModel {

    private int id;
    private String name;
    private String description;
    private byte[] logo;
    private AddressReadModel address;
    private CategoryReadModel category;
    private UserReadModel owner;

}
