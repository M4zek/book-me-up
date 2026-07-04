package com.m4zek.backend.controller;

import com.m4zek.backend.mapper.AddressMapper;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.dto.read.AddressResponse;
import com.m4zek.backend.model.dto.write.AddressRequest;
import com.m4zek.backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping(value = "/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    public AddressController(AddressService addressService, AddressMapper addressMapper)
    {
        this.addressService = addressService;
        this.addressMapper = addressMapper;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<AddressResponse> addAddress(@RequestBody @Valid AddressRequest addressRequest)
    {
        Address address =  this.addressService.createNewAddress(addressRequest);
        int addressId = address.getId();
        URI location = URI.create("/api/v1/addresses/" + addressId);
        return ResponseEntity.created(location).body(this.addressMapper.addressToAddressResponse(address));
    }


    @PatchMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable("addressId") int id,
            @RequestBody @Valid AddressRequest addressRequest)
    {
        Address address = this.addressService.updateAddress(id, addressRequest);
        return ResponseEntity.ok(this.addressMapper.addressToAddressResponse(address));
    }
}
