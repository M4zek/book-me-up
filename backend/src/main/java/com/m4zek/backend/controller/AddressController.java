package com.m4zek.backend.controller;

import com.m4zek.backend.mapper.AddressMapper;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.dto.read.AddressResponse;
import com.m4zek.backend.model.dto.write.AddressRequest;
import com.m4zek.backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping("api/v1/addresses")
public class AddressController {

    // TODO This controller will be remove because all operation with addresses will be processing in CompanyController

    private final AddressService addressService;

    public AddressController(AddressService addressService)
    {
        this.addressService = addressService;
    }


    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@RequestBody @Valid AddressRequest addressRequest)
    {
        Address address =  this.addressService.createNewAddress(addressRequest);
        int addressId = address.getId();
        URI location = URI.create("/api/v1/addresses/" + addressId);
        return ResponseEntity.created(location).body(AddressMapper.addressToAddressResponse(address));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable("id") int id,
                                           @RequestBody @Valid AddressRequest addressRequest)
    {
        Address address = this.addressService.updateAddress(id, addressRequest);
        return ResponseEntity.ok(AddressMapper.addressToAddressResponse(address));
    }

}
