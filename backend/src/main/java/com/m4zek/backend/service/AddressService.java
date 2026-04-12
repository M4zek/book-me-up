package com.m4zek.backend.service;

import com.m4zek.backend.exception.AddressNotFoundException;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.dto.write.AddressRequest;
import com.m4zek.backend.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }
    

    public Address createNewAddress(AddressRequest addressRequest) {
        Address address = new Address(
                addressRequest.getCity(),
                addressRequest.getPostalCode(),
                addressRequest.getStreet(),
                addressRequest.getBuildingNumber()
        );
        return addressRepository.save(address);
    }

    public Address updateAddress(int id, AddressRequest addressRequest) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("No address found with id " + id));

        address.update(addressRequest);
        return addressRepository.save(address);
    }

}
