package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Address;
import com.m4zek.backend.repository.AddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlAddressRepository extends AddressRepository, JpaRepository<Address, Integer> {

}
