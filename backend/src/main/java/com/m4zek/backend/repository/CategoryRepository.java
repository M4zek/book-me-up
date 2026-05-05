package com.m4zek.backend.repository;

import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.dto.read.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category categories);

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    @Query("""
        SELECT new com.m4zek.backend.model.dto.read.CategoryResponse(
            c.id,
            c.name,
            COUNT(comp)
        )
        FROM Categories c
        LEFT JOIN c.companies comp
        GROUP BY c.id, c.name
    """)
    Page<CategoryResponse> findAllToDTO(Pageable pageable);

    Optional<Category> findById(int id);
}
