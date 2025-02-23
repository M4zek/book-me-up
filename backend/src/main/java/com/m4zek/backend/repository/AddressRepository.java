package com.m4zek.backend.repository;

import com.m4zek.backend.model.Address;

import java.util.Optional;

public interface AddressRepository {

    Address save(Address address);

    Optional<Address> findById(int id);

}
