package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.CategoryReadModel;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Company> companies = new ArrayList<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }


    public CategoryReadModel toReadModel() {
        return CategoryReadModel.builder()
                .id(this.id)
                .name(this.name)
                .numberOfCompanies(this.companies.size())
                .build();
    }

    public void updateName(String name){
        this.name = name;
    }
}
