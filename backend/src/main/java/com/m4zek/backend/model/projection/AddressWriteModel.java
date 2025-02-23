package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressWriteModel {

    private String city;

    private String postalCode;

    private String street;

    private String buildingNumber;

    public Address toEntity() {
        return new Address(this.city, this.postalCode, this.street, this.buildingNumber);
    }

}
