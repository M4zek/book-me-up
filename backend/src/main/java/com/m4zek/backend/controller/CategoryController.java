package com.m4zek.backend.controller;

import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.projection.CategoryReadModel;
import com.m4zek.backend.model.projection.CategoryWriteModel;
import com.m4zek.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryReadModel> createNewCategory(@RequestBody CategoryWriteModel category) {
        Category newCategory = this.categoryService.saveCategory(category);
        int categoryId = newCategory.toReadModel().getId();
        URI location = URI.create("/api/v1/categories/" + categoryId);
        return ResponseEntity.created(location).body(newCategory.toReadModel());
    }

    @GetMapping
    public ResponseEntity<List<CategoryReadModel>> getAllCategories() {
        List<CategoryReadModel> categoryReadModelList = this.categoryService.findAllCategories();
        return ResponseEntity.ok(categoryReadModelList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryReadModel> updateCategoryName(
                @PathVariable("id") int id, @RequestBody CategoryWriteModel categoryWriteModel) {
        return ResponseEntity.ok(this.categoryService.updateCategoryName(id, categoryWriteModel));
    }

}
