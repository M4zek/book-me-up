package com.m4zek.backend.service;

import com.m4zek.backend.exception.CategoryExistsException;
import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.projection.CategoryReadModel;
import com.m4zek.backend.model.projection.CategoryWriteModel;
import com.m4zek.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category saveCategory(CategoryWriteModel categoryWriteModel) {
        this.categoryRepository.findByName(categoryWriteModel.getName()).ifPresent(category -> {
            throw new CategoryExistsException("Category with name " + categoryWriteModel.getName() + " already exists");
        });

        Category category = categoryWriteModel.toEntity();
        return this.categoryRepository.save(category);
    }

    public List<CategoryReadModel> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::toReadModel)
                .toList();
    }

    public CategoryReadModel updateCategoryName(int id, CategoryWriteModel categoryWriteModel) {
        this.categoryRepository.findByName(categoryWriteModel.getName()).ifPresent(category -> {
            throw new CategoryExistsException("Category with name " + categoryWriteModel.getName() + " already exists");
        });

        Category currentCategory = this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        currentCategory.updateName(categoryWriteModel.getName());
        currentCategory = this.categoryRepository.save(currentCategory);

        return currentCategory.toReadModel();
    }

}
