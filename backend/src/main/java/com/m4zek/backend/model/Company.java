package com.m4zek.backend.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "companies")
public class Company extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private Double averageRating;
    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoredFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyHours> companyHoursList = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyOffer> companyOffers = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyUserRole> users = new HashSet<>();

    public Company() {}

    public Company(String name, String description, Category category, Address address) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.averageRating = 0.0;
    }

    public void addUserRole(CompanyUserRole companyUserRole){
        this.users.add(companyUserRole);
    }

    public void assignLogoFile(StoredFile logo){
        this.images.add(logo);
    }

    public void assignImage(StoredFile img){
        this.images.add(img);
    }

    public void assignAddress(Address address){
        this.address = address;
    }

    public void assignCompanyHours(List<CompanyHours> companyHours){
        this.companyHoursList = companyHours;
    }

    public void addCompanyHour(CompanyHours companyHours){
        this.companyHoursList.add(companyHours);
    }

    public StoredFile getLogoFile(){
        return this.images.stream()
                .filter(img -> img.getType() == FileType.IMG_COMPANY_LOGO)
                .findFirst().orElse(null);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public Address getAddress() {
        return address;
    }

    public Category getCategory() {
        return category;
    }

    public List<CompanyHours> getCompanyHoursList() {
        return companyHoursList;
    }

    public List<CompanyOffer> getCompanyOffers() {
        return companyOffers;
    }

    public Set<CompanyUserRole> getUsers() {
        return users;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
