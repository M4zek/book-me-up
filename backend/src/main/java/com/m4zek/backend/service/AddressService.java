package com.m4zek.backend.service;

import com.m4zek.backend.exception.AddressNotFoundException;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.projection.AddressWriteModel;
import com.m4zek.backend.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }
    

    public Address createNewAddress(AddressWriteModel addressWriteModel) {
        Address address = addressWriteModel.toEntity();
        return addressRepository.save(address);
    }

    public Address updateAddress(int id, AddressWriteModel addressWriteModel) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("No address found with id " + id));

        address.update(addressWriteModel);
        return addressRepository.save(address);
    }

}
