package com.m4zek.backend.model.dto.read;


import com.m4zek.backend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private int id;
    private String name;
    private long numberOfCompanies;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.numberOfCompanies = category.getNumberOfCompanies();
    }
}
