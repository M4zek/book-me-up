package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AddressReadModel {

    private int id;

    private String city;

    private String postalCode;

    private String street;

    private String buildingNumber;

}
