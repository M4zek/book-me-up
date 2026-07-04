package com.m4zek.backend.mapper;

import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.dto.read.AddressResponse;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    private AddressMapper() {}

    public AddressResponse addressToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .buildingNumber(address.getBuildingNumber())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .build();
    }

}
