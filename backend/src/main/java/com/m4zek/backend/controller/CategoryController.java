package com.m4zek.backend.controller;

import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.dto.read.CategoryResponse;
import com.m4zek.backend.model.dto.write.CategoryRequest;
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
    public ResponseEntity<CategoryResponse> createNewCategory(@RequestBody @Valid CategoryRequest category) {
        Category newCategory = this.categoryService.saveCategory(category);
        URI location = URI.create("/api/v1/categories/" + newCategory.getId());
        return ResponseEntity.created(location).body(new CategoryResponse(newCategory));
    }

    @PatchMapping("/v1/categories/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategoryName(
                @PathVariable("id") int id,
                @RequestBody @Valid CategoryRequest categoryRequest) {
        return ResponseEntity.ok(this.categoryService.updateCategoryName(id, categoryRequest));
    }


//    *******************************************
//         ENDPOINTS FOR EVERYONE (PUBLIC)
//    *******************************************

    @GetMapping("/public/categories")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        Page<CategoryResponse> categoryReadModelList = this.categoryService.findAllCategories(pageable);
        return ResponseEntity.ok(categoryReadModelList);
    }

}
