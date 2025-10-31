package com.m4zek.backend.model.dto.write;

import com.m4zek.backend.model.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CategoryWriteModel {

    @NotBlank(message = "Category name must not be empty")
    private String name;

    public Category toEntity() {
        this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase();
        return new Category(this.name);
    }
}
