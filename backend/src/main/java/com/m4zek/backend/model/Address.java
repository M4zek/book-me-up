package com.m4zek.backend.model;

import com.m4zek.backend.model.dto.write.AddressRequest;
import jakarta.persistence.*;

@Entity(name = "addresses")
public class Address extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }



    public void update(AddressRequest addressRequest) {
        if(!this.city.equals(addressRequest.getCity()) && !addressRequest.getCity().isEmpty())
            this.city = addressRequest.getCity();

        if (!this.postalCode.equals(addressRequest.getPostalCode()) && !addressRequest.getPostalCode().isEmpty())
            this.postalCode = addressRequest.getPostalCode();

        if (!this.street.equals(addressRequest.getStreet()) && !addressRequest.getStreet().isEmpty())
            this.street = addressRequest.getStreet();

        if (!this.buildingNumber.equals(addressRequest.getBuildingNumber()) && !addressRequest.getBuildingNumber().isEmpty())
            this.buildingNumber = addressRequest.getBuildingNumber();
    }

    public void assignCompany(Company company) {
        this.company = company;
    }

}
