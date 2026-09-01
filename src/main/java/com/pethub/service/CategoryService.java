package com.pethub.service;

import com.pethub.dto.request.CategoryRequest;
import com.pethub.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    // Pet Categories
    List<CategoryResponse> getAllPetCategories();
    CategoryResponse getPetCategoryById(Long id);
    CategoryResponse getPetCategoryBySlug(String slug);
    CategoryResponse createPetCategory(CategoryRequest request);
    CategoryResponse updatePetCategory(Long id, CategoryRequest request);
    void deletePetCategory(Long id);

    // Product Categories
    List<CategoryResponse> getAllProductCategories();
    CategoryResponse getProductCategoryById(Long id);
    CategoryResponse getProductCategoryBySlug(String slug);
    CategoryResponse createProductCategory(CategoryRequest request);
    CategoryResponse updateProductCategory(Long id, CategoryRequest request);
    void deleteProductCategory(Long id);
}
