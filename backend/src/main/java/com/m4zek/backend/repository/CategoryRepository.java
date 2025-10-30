package com.m4zek.backend.repository;

import com.m4zek.backend.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category categories);

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findById(int id);
}
