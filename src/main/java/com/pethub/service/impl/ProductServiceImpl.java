package com.pethub.service.impl;

import com.pethub.dto.request.ProductRequest;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.ProductResponse;
import com.pethub.entity.PetCategory;
import com.pethub.entity.Product;
import com.pethub.entity.ProductCategory;
import com.pethub.entity.ProductImage;
import com.pethub.exception.DuplicateResourceException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.ProductMapper;
import com.pethub.repository.PetCategoryRepository;
import com.pethub.repository.ProductCategoryRepository;
import com.pethub.repository.ProductImageRepository;
import com.pethub.repository.ProductRepository;
import com.pethub.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              PetCategoryRepository petCategoryRepository,
                              ProductCategoryRepository productCategoryRepository,
                              ProductImageRepository productImageRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.petCategoryRepository = petCategoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.productMapper = productMapper;
    }

    private String slugify(String input) {
        return input.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProducts(Long petCategoryId, Long productCategoryId, String brand,
                                                      BigDecimal minPrice, BigDecimal maxPrice, boolean inStockOnly,
                                                      String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"),
                (sortBy != null && !sortBy.isEmpty()) ? sortBy : "createdAt");

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String trimmedBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;

        Page<Product> productPage = productRepository.filterProducts(
                petCategoryId,
                productCategoryId,
                trimmedBrand,
                minPrice,
                maxPrice,
                inStockOnly,
                trimmedKeyword,
                pageable
        );

        List<ProductResponse> responses = productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findTop8ByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productRepository.findAllDistinctBrands();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        String baseSlug = slugify(request.getName());
        String slug = baseSlug;
        int count = 1;
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        if (productRepository.existsBySku(request.getSku().trim())) {
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists.");
        }

        PetCategory petCategory = petCategoryRepository.findById(request.getPetCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet category not found with id: " + request.getPetCategoryId()));

        ProductCategory productCategory = productCategoryRepository.findById(request.getProductCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + request.getProductCategoryId()));

        Product product = new Product(
                request.getName().trim(),
                slug,
                request.getDescription().trim(),
                request.getPrice(),
                request.getDiscountPrice(),
                request.getBrand() != null ? request.getBrand().trim() : "PET HUB",
                request.getSku().trim().toUpperCase(),
                request.getStockQuantity(),
                petCategory,
                productCategory,
                request.isActive(),
                request.isFeatured()
        );

        Product savedProduct = productRepository.save(product);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int order = 0;
            for (String url : request.getImageUrls()) {
                ProductImage image = new ProductImage(savedProduct, url, order == 0, order++);
                productImageRepository.save(image);
            }
        }

        return productMapper.toResponse(productRepository.findById(savedProduct.getId()).orElse(savedProduct));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equalsIgnoreCase(request.getSku().trim()) && productRepository.existsBySku(request.getSku().trim())) {
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists.");
        }

        PetCategory petCategory = petCategoryRepository.findById(request.getPetCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet category not found with id: " + request.getPetCategoryId()));

        ProductCategory productCategory = productCategoryRepository.findById(request.getProductCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + request.getProductCategoryId()));

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setBrand(request.getBrand() != null ? request.getBrand().trim() : product.getBrand());
        product.setSku(request.getSku().trim().toUpperCase());
        product.setStockQuantity(request.getStockQuantity());
        product.setPetCategory(petCategory);
        product.setProductCategory(productCategory);
        product.setActive(request.isActive());
        product.setFeatured(request.isFeatured());

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        // Soft delete / deactivate
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse addProductImage(Long productId, String imageUrl, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (isPrimary && product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                img.setPrimary(false);
                productImageRepository.save(img);
            }
        }

        int nextOrder = product.getImages() != null ? product.getImages().size() : 0;
        ProductImage newImage = new ProductImage(product, imageUrl, isPrimary || nextOrder == 0, nextOrder);
        productImageRepository.save(newImage);

        return productMapper.toResponse(productRepository.findById(productId).get());
    }

    @Override
    @Transactional
    public void deleteProductImage(Long imageId) {
        if (!productImageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image not found with id: " + imageId);
        }
        productImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public ProductResponse setPrimaryImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        for (ProductImage img : product.getImages()) {
            img.setPrimary(img.getId().equals(imageId));
            productImageRepository.save(img);
        }

        return productMapper.toResponse(productRepository.findById(productId).get());
    }
}
