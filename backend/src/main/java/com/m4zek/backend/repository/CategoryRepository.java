package com.m4zek.backend.repository;

import com.m4zek.backend.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category categories);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    Optional<Category> findById(int id);
}
