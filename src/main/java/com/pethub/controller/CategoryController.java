package com.pethub.controller;

import com.pethub.dto.response.ApiResponse;
import com.pethub.dto.response.CategoryResponse;
import com.pethub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/pet-categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllPetCategories() {
        List<CategoryResponse> categories = categoryService.getAllPetCategories();
        return ResponseEntity.ok(ApiResponse.success("Pet categories fetched successfully", categories));
    }

    @GetMapping("/pet-categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getPetCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getPetCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Pet category fetched successfully", category));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllProductCategories() {
        List<CategoryResponse> categories = categoryService.getAllProductCategories();
        return ResponseEntity.ok(ApiResponse.success("Product categories fetched successfully", categories));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getProductCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getProductCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Product category fetched successfully", category));
    }
}
