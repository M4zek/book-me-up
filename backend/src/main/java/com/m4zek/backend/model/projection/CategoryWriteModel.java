package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CategoryWriteModel {
    private String name;

    public Category toEntity() {
        this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase();
        return new Category(this.name);
    }
}
