package com.m4zek.backend.controller;

import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.dto.read.CategoryReadModel;
import com.m4zek.backend.model.dto.write.CategoryWriteModel;
import com.m4zek.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

//    *******************************************
//            ENDPOINTS FOR LOGGED USERS
//    *******************************************

    @PostMapping("/v1/categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryReadModel> createNewCategory(@RequestBody @Valid CategoryWriteModel category) {
        Category newCategory = this.categoryService.saveCategory(category);
        int categoryId = newCategory.toReadModel().getId();
        URI location = URI.create("/api/v1/categories/" + categoryId);
        return ResponseEntity.created(location).body(newCategory.toReadModel());
    }

    @PatchMapping("/v1/categories/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryReadModel> updateCategoryName(
                @PathVariable("id") int id,
                @RequestBody @Valid CategoryWriteModel categoryWriteModel) {
        return ResponseEntity.ok(this.categoryService.updateCategoryName(id, categoryWriteModel));
    }


//    *******************************************
//         ENDPOINTS FOR EVERYONE (PUBLIC)
//    *******************************************

    @GetMapping("/public/categories")
    public ResponseEntity<Page<CategoryReadModel>> getAllCategories(Pageable pageable) {
        Page<CategoryReadModel> categoryReadModelList = this.categoryService.findAllCategories(pageable);
        return ResponseEntity.ok(categoryReadModelList);
    }

}
