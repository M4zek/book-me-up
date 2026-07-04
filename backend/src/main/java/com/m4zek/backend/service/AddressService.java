package com.m4zek.backend.service;

import com.m4zek.backend.exception.AddressNotFoundException;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.write.AddressRequest;
import com.m4zek.backend.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final static Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void assignAddress(Company company, AddressRequest request) {
        Address address = new Address(
                request.getCity(),
                request.getPostalCode(),
                request.getStreet(),
                request.getBuildingNumber()
        );
        address.assignCompany(company);
        company.assignAddress(address);
        this.save(address);
    }

    public Address createNewAddress(AddressRequest addressRequest) {
        Address address = new Address(
                addressRequest.getCity(),
                addressRequest.getPostalCode(),
                addressRequest.getStreet(),
                addressRequest.getBuildingNumber()
        );
        return this.save(address);
    }

    public Address save(Address address){
        Address savedAddress = this.addressRepository.save(address);
        logger.info("Address has been saved [{}]", savedAddress.getId());
        return savedAddress;
    }

    public Address updateAddress(int id, AddressRequest addressRequest) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("No address found with id " + id));

        this.update(address, addressRequest);
        return addressRepository.save(address);
    }


    private void update(Address currentAddress, AddressRequest addressRequest) {
        if(!currentAddress.getCity().equals(addressRequest.getCity()) && !addressRequest.getCity().isEmpty())
            currentAddress.setCity(addressRequest.getCity());

        if (!currentAddress.getPostalCode().equals(addressRequest.getPostalCode()) && !addressRequest.getPostalCode().isEmpty())
            currentAddress.setPostalCode(addressRequest.getPostalCode());

        if (!currentAddress.getStreet().equals(addressRequest.getStreet()) && !addressRequest.getStreet().isEmpty())
            currentAddress.setStreet(addressRequest.getStreet());

        if (!currentAddress.getBuildingNumber().equals(addressRequest.getBuildingNumber()) && !addressRequest.getBuildingNumber().isEmpty())
            currentAddress.setBuildingNumber(addressRequest.getBuildingNumber());
    }

}
