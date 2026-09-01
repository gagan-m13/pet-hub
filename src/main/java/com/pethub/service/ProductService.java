package com.pethub.service;

import com.pethub.dto.request.ProductRequest;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    PagedResponse<ProductResponse> getProducts(
            Long petCategoryId,
            Long productCategoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean inStockOnly,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    List<ProductResponse> getFeaturedProducts();

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySlug(String slug);

    List<String> getAllBrands();

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    ProductResponse addProductImage(Long productId, String imageUrl, boolean isPrimary);

    void deleteProductImage(Long imageId);

    ProductResponse setPrimaryImage(Long productId, Long imageId);
}
