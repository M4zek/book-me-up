package com.m4zek.backend.model;


import com.m4zek.backend.model.projection.CompanyReadModel;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "companies")
public class Company extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Lob
    private byte[] logo;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<PortfolioImage> portfolioImages = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyHours> companyHoursList = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyOffer> companyOffers = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyUserRole> users = new HashSet<>();

    public Company() {}

    public Company(String name, String description, byte[] logo, Category category, Address address) {
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.category = category;
        this.address = address;
    }

    public void changeName(String newName) {
        if(!this.name.equals(newName) && !newName.isEmpty())
            this.name = newName;
    }

    public void changeDescription(String description) {
        if(!this.description.equals(description) && !description.isEmpty())
            this.description = description;
    }

    public void changeLogo(byte[] newLogo) {
        this.logo = newLogo;
    }

    public void assignCategory(Category newCategory) {
        this.category = newCategory;
    }

    public void assignPortfolioImages(PortfolioImage newPortfolioImage) {
        this.portfolioImages.add(newPortfolioImage);
    }

    public CompanyReadModel toReadModel() {
        return CompanyReadModel
                .builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .logo(this.logo)
                .address(address.toReadModel())
                .category(category.toReadModel())
                .owner(
                   this.users.stream()
                            .filter(item -> item.getRole().getName().equals("COMPANY_OWNER"))
                            .findFirst()
                            .map(item -> item.getUsers().toUserReadModel())
                            .orElse(null)
                )
                .build();
    }
}
