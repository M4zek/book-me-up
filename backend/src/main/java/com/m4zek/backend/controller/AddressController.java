package com.m4zek.backend.controller;

import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.projection.AddressReadModel;
import com.m4zek.backend.model.projection.AddressWriteModel;
import com.m4zek.backend.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService)
    {
        this.addressService = addressService;
    }


    @PostMapping
    public ResponseEntity<AddressReadModel> addAddress(@RequestBody AddressWriteModel addressWriteModel)
    {
        Address responseEntity =  this.addressService.createNewAddress(addressWriteModel);
        int addressId = responseEntity.toReadModel().getId();
        URI location = URI.create("/api/v1/addresses/" + addressId);
        return ResponseEntity.created(location).body(responseEntity.toReadModel());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") int id,
                                           @RequestBody AddressWriteModel addressWriteModel)
    {
        AddressReadModel responseEntity = this.addressService.updateAddress(id, addressWriteModel).toReadModel();
        return ResponseEntity.ok(responseEntity);
    }

}
