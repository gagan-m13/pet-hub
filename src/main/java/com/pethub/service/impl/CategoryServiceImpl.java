package com.pethub.service.impl;

import com.pethub.dto.request.CategoryRequest;
import com.pethub.dto.response.CategoryResponse;
import com.pethub.entity.PetCategory;
import com.pethub.entity.ProductCategory;
import com.pethub.exception.DuplicateResourceException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.CategoryMapper;
import com.pethub.repository.PetCategoryRepository;
import com.pethub.repository.ProductCategoryRepository;
import com.pethub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final PetCategoryRepository petCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(PetCategoryRepository petCategoryRepository,
                               ProductCategoryRepository productCategoryRepository,
                               CategoryMapper categoryMapper) {
        this.petCategoryRepository = petCategoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.categoryMapper = categoryMapper;
    }

    private String slugify(String input) {
        return input.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllPetCategories() {
        return petCategoryRepository.findAll().stream()
                .map(categoryMapper::toPetCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getPetCategoryById(Long id) {
        PetCategory category = petCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet category not found with id: " + id));
        return categoryMapper.toPetCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getPetCategoryBySlug(String slug) {
        PetCategory category = petCategoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Pet category not found with slug: " + slug));
        return categoryMapper.toPetCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createPetCategory(CategoryRequest request) {
        String slug = slugify(request.getName());
        if (petCategoryRepository.existsByName(request.getName()) || petCategoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Pet category with name " + request.getName() + " already exists.");
        }

        PetCategory category = new PetCategory(request.getName(), slug, request.getDescription(), request.getImageUrl());
        return categoryMapper.toPetCategoryResponse(petCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updatePetCategory(Long id, CategoryRequest request) {
        PetCategory category = petCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet category not found with id: " + id));

        category.setName(request.getName());
        category.setSlug(slugify(request.getName()));
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        return categoryMapper.toPetCategoryResponse(petCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deletePetCategory(Long id) {
        if (!petCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet category not found with id: " + id);
        }
        petCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllProductCategories() {
        return productCategoryRepository.findAll().stream()
                .map(categoryMapper::toProductCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getProductCategoryById(Long id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));
        return categoryMapper.toProductCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getProductCategoryBySlug(String slug) {
        ProductCategory category = productCategoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with slug: " + slug));
        return categoryMapper.toProductCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createProductCategory(CategoryRequest request) {
        String slug = slugify(request.getName());
        if (productCategoryRepository.existsByName(request.getName()) || productCategoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Product category with name " + request.getName() + " already exists.");
        }

        ProductCategory category = new ProductCategory(request.getName(), slug, request.getDescription(), request.getImageUrl());
        return categoryMapper.toProductCategoryResponse(productCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateProductCategory(Long id, CategoryRequest request) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        category.setName(request.getName());
        category.setSlug(slugify(request.getName()));
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        return categoryMapper.toProductCategoryResponse(productCategoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteProductCategory(Long id) {
        if (!productCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product category not found with id: " + id);
        }
        productCategoryRepository.deleteById(id);
    }
}
