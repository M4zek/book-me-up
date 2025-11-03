package com.m4zek.backend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Categories")
public class Category extends BaseEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Company> companies = new ArrayList<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public int getNumberOfCompanies() {
        return companies.size();
    }

    public void updateName(String name){
        this.name = name;
    }
}
