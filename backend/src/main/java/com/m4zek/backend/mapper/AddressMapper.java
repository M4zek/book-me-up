package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.dto.read.AddressResponse;

public class AddressMapper {

    private AddressMapper() {}

    public static AddressResponse addressToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .buildingNumber(address.getBuildingNumber())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .build();
    }

}
