package com.m4zek.backend;


import com.m4zek.backend.exception.AddressNotFoundException;
import com.m4zek.backend.model.Address;
import com.m4zek.backend.model.projection.AddressWriteModel;
import com.m4zek.backend.repository.AddressRepository;
import com.m4zek.backend.service.AddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class AddressServiceTest {

    @Test
    @DisplayName("Should throw AddressNotFoundException when address id not found in database")
    void updateAddress_addressNotFoundWithGivenId_throwsAddressNotFoundException() {

        //given
        var mockAddressService = mock(AddressRepository.class);
        when(mockAddressService.findById(anyInt())).thenReturn(Optional.empty());

        // system under test
        var toTest = new AddressService(mockAddressService);

        // when
        var exception = catchThrowable(() -> toTest.updateAddress(0, null));

        // then
        assertThat(exception)
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("No address found");

    }

    @Test
    @DisplayName("Should successfully update existing address based on given id")
    void updateAddress_addressFoundWithGivenId_updatesAddress() {
        // given
        var newAddressData = AddressWriteModel.builder()
                .city("New City")
                .street("New Street")
                .postalCode("22-222")
                .buildingNumber("33")
                .build();

        var mockAddressRepository = mock(AddressRepository.class);
        var currentAddress = new Address("City", "00-000", "Street", "0");

        when(mockAddressRepository.findById(anyInt())).thenReturn(Optional.of(currentAddress));
        when(mockAddressRepository.save(any(Address.class))).thenReturn(currentAddress);

        // system under test
        var toTest = new AddressService(mockAddressRepository);

        //when
        var updatedAddress = toTest.updateAddress(0, newAddressData);

        //then
        assertThat(updatedAddress).isEqualTo(currentAddress);
    }


    @Test
    @DisplayName("Should create new address")
    void createAddress_addressCreated() {
        // given
        var newAddressData = AddressWriteModel.builder()
                .city("New City")
                .street("New Street")
                .postalCode("22-222")
                .buildingNumber("33")
                .build();

        var mockAddressRepository = mock(AddressRepository.class);
        when(mockAddressRepository.save(any(Address.class))).thenReturn(newAddressData.toEntity());

        // system under test
        var toTest = new AddressService(mockAddressRepository);

        //when
        var newAddress = toTest.createNewAddress(newAddressData);

        // then
        assertThat(newAddress.toReadModel().getBuildingNumber()).isEqualTo(newAddressData.getBuildingNumber());
        assertThat(newAddress.toReadModel().getStreet()).isEqualTo(newAddressData.getStreet());
        assertThat(newAddress.toReadModel().getCity()).isEqualTo(newAddressData.getCity());
        assertThat(newAddress.toReadModel().getPostalCode()).isEqualTo(newAddressData.getPostalCode());

        verify(mockAddressRepository, times(1)).save(any(Address.class));
    }

}
