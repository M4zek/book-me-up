package com.m4zek.backend.model;

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

    @OneToOne(cascade = CascadeType.ALL)
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

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public void assignCompany(Company company) {
        this.company = company;
    }

}
