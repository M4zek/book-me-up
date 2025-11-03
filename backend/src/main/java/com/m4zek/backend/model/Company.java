package com.m4zek.backend.model;


import com.m4zek.backend.model.dto.read.*;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public void addUserRole(CompanyUserRole companyUserRole){
        this.users.add(companyUserRole);
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

    public byte[] getLogo() {
        return logo;
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

    public ReviewStatisticsResponse getCompanyReviewStatistics() {
        List<Review> allReviews = companyOffers.stream()
                .flatMap(offer -> offer.getReviews().stream())
                .filter(r -> r.toReadModel().getRating() != null)
                .toList();

        int totalReviews = allReviews.size();

        double averageRating = allReviews.stream()
                .mapToInt(review -> review.toReadModel().getRating())
                .average()
                .orElse(0.0);

        Map<Integer, Long> ratingCountMap = allReviews.stream()
                .collect(Collectors.groupingBy(review -> review.toReadModel().getRating(), Collectors.counting()));

        Map<Integer, Integer> completeRatingMap = IntStream.rangeClosed(1, 5)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> ratingCountMap.getOrDefault(i, 0L).intValue()
                ));

        List<Map<Integer, Integer>> ratingCounts = List.of(completeRatingMap);

        return ReviewStatisticsResponse.builder()
                .rating(averageRating)
                .totalReviews(totalReviews)
                .ratingCounts(ratingCounts)
                .build();
    }
}
