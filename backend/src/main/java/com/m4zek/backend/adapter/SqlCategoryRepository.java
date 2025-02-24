package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Category;
import com.m4zek.backend.repository.CategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCategoryRepository extends CategoryRepository, JpaRepository<Category, Integer> {
}
