package com.m4zek.backend.model;

import com.m4zek.backend.model.dto.read.AddressReadModel;
import com.m4zek.backend.model.dto.write.AddressWriteModel;
import jakarta.persistence.*;

@Entity(name = "addresses")
public class Address extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String city;

    private String postalCode;

    private String street;

    private String buildingNumber;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Address(){}

    public Address(String city, String postalCode, String street, String buildingNumber) {
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }


    public AddressReadModel toReadModel() {
        return AddressReadModel.builder()
                .id(this.id)
                .city(this.city)
                .postalCode(this.postalCode)
                .street(this.street)
                .buildingNumber(this.buildingNumber)
                .build();
    }


    public void update(AddressWriteModel addressWriteModel) {
        if(!this.city.equals(addressWriteModel.getCity()) && !addressWriteModel.getCity().isEmpty())
            this.city = addressWriteModel.getCity();

        if (!this.postalCode.equals(addressWriteModel.getPostalCode()) && !addressWriteModel.getPostalCode().isEmpty())
            this.postalCode = addressWriteModel.getPostalCode();

        if (!this.street.equals(addressWriteModel.getStreet()) && !addressWriteModel.getStreet().isEmpty())
            this.street = addressWriteModel.getStreet();

        if (!this.buildingNumber.equals(addressWriteModel.getBuildingNumber()) && !addressWriteModel.getBuildingNumber().isEmpty())
            this.buildingNumber = addressWriteModel.getBuildingNumber();
    }

    public void assignCompany(Company company) {
        this.company = company;
    }

}
