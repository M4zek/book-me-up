package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.CategoryReadModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }


    public CategoryReadModel toReadModel() {
        return CategoryReadModel.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

    public void updateName(String name){
        this.name = name;
    }
}
