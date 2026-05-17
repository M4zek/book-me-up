package com.m4zek.backend.service;

import com.m4zek.backend.exception.CategoryExistsException;
import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.dto.read.CategoryResponse;
import com.m4zek.backend.model.dto.write.CategoryRequest;
import com.m4zek.backend.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category saveCategory(CategoryRequest categoryRequest) {
        this.categoryRepository.findByName(categoryRequest.getName()).ifPresent(category -> {
            throw new CategoryExistsException("Category with name " + categoryRequest.getName() + " already exists");
        });

        Category category = categoryRequest.toEntity();
        return this.categoryRepository.save(category);
    }


    public Page<CategoryResponse> findAllCategories(Pageable pageable) {
        Page<CategoryResponse> pageCategory = categoryRepository.findAllToDTO(pageable);
        return new PageImpl<>(pageCategory.stream().toList(), pageable, pageCategory.getTotalElements());
    }

    public CategoryResponse updateCategoryName(int id, CategoryRequest categoryRequest) {
        this.categoryRepository.findByName(categoryRequest.getName()).ifPresent(category -> {
            throw new CategoryExistsException("Category with name " + categoryRequest.getName() + " already exists");
        });

        Category currentCategory = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        currentCategory.updateName(categoryRequest.getName());
        currentCategory = this.categoryRepository.save(currentCategory);

        return new CategoryResponse(currentCategory);
    }

}
