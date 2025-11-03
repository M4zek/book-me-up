package com.m4zek.backend;

import com.m4zek.backend.exception.CategoryExistsException;
import com.m4zek.backend.exception.CategoryNotFoundException;
import com.m4zek.backend.model.Category;
import com.m4zek.backend.model.dto.read.CategoryResponse;
import com.m4zek.backend.model.dto.write.CategoryRequest;
import com.m4zek.backend.repository.CategoryRepository;
import com.m4zek.backend.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {


    @Test
    @DisplayName("Should create new category")
    void createCategory_categoryCreated() {
        // given
        var categoryDTO = CategoryRequest.builder()
                .name("New category")
                .build();


        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.save(any(Category.class))).thenReturn(categoryDTO.toEntity());

        //system under test
        var toTest = new CategoryService(mockCategoryRepository);

        // when
        var newCategory = toTest.saveCategory(categoryDTO);

        //then
        assertThat(newCategory.getName()).isEqualTo(categoryDTO.getName());
        verify(mockCategoryRepository, times(1)).save(any(Category.class));

    }

    @Test
    @DisplayName("saveCategory() - Should throw CategoryExistsException when an entity with that category name already exists")
    void createCategory_categoryAlreadyExists() {
        //given
        var categoryDTO = CategoryRequest.builder()
                .name("Category1")
                .build();

        var existingCategory = new Category("Category1");

        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.findByName(any(String.class))).thenReturn(Optional.of(existingCategory));

        //system under test
        var toTest = new CategoryService(mockCategoryRepository);

        //when
        var exception = catchThrowable(() -> toTest.saveCategory(categoryDTO));

        //then
        assertThat(exception)
                .isInstanceOf(CategoryExistsException.class)
                .hasMessageContaining("already exists");

        verify(mockCategoryRepository, times(1)).findByName(any(String.class));
    }

    @Test
    @DisplayName("Should return list of category read models")
    void findAllCategories_categoryFound() {
        // given
        Category category1 = new Category("Category1");
        Category category2 = new Category("Category2");
        var pageable = PageRequest.of(0, 10);

        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category1, category2)));

        // system under test
        var toTest = new CategoryService(mockCategoryRepository);



        // when
        Page<CategoryResponse> categories = toTest.findAllCategories(pageable);
        List<CategoryResponse> content = categories.getContent();

        // then
        assertThat(content)
                .hasSize(2)
                .extracting(CategoryResponse::getName)
                .containsExactly("Category1", "Category2");

        verify(mockCategoryRepository, times(1)).findAll(pageable);
    }


    @Test
    @DisplayName("updateCategoryName() - Should throw CategoryExistsException when an entity with that category name already exists")
    void updateCategoryName_shouldThrowCategoryExistException() {
        //given
        var categoryDTO = CategoryRequest.builder()
                .name("Category1")
                .build();

        var existingCategory = new Category("Category1");

        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.findByName(any(String.class))).thenReturn(Optional.of(existingCategory));

        //system under test
        var toTest = new CategoryService(mockCategoryRepository);

        //when
        var exception = catchThrowable(() -> toTest.updateCategoryName(0, categoryDTO));

        //then
        assertThat(exception)
                .isInstanceOf(CategoryExistsException.class)
                .hasMessageContaining("already exists");

        verify(mockCategoryRepository, times(1)).findByName(any(String.class));
    }


    @Test
    @DisplayName("Should throw CategoryNotFoundException when an entity with category id no exists")
    void updateCategoryName_shouldThrowCategoryNotFoundException() {
        // given
        int categoryId = 1;
        var categoryDTO = CategoryRequest.builder()
                .name("Category1")
                .build();
        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        // system under test
        var toTest = new CategoryService(mockCategoryRepository);

        // when
        var exception = catchThrowable(() -> toTest.updateCategoryName(categoryId, categoryDTO));

        // then
        assertThat(exception)
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category with id " + categoryId + " not found");

        verify(mockCategoryRepository, times(1)).findById(any(Integer.class));
    }

    @Test
    @DisplayName("Should update category successfully based on DTO")
    void updateCategory_success() {
        //given
        var categoryId = 0;
        var categoryDTO = CategoryRequest.builder()
                .name("Category1")
                .build();

        var currentCategory = new Category("Old category");

        var mockCategoryRepository = mock(CategoryRepository.class);
        when(mockCategoryRepository.findByName(any(String.class))).thenReturn(Optional.empty());
        when(mockCategoryRepository.findById(any(Integer.class))).thenReturn(Optional.of(currentCategory));
        when(mockCategoryRepository.save(any(Category.class))).thenReturn(currentCategory);

        // system under test
        var toTest = new CategoryService(mockCategoryRepository);

        //when
        CategoryResponse updatedCategory = toTest.updateCategoryName(categoryId, categoryDTO);

        // then
        assertThat(updatedCategory.getName()).isEqualTo(categoryDTO.getName());

        verify(mockCategoryRepository, times(1)).findByName(any(String.class));
        verify(mockCategoryRepository, times(1)).findById(any(Integer.class));
        verify(mockCategoryRepository, times(1)).save(any(Category.class));
    }


}
