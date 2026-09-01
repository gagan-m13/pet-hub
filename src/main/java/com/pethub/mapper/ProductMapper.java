package com.pethub.mapper;

import com.pethub.dto.response.ProductImageResponse;
import com.pethub.dto.response.ProductResponse;
import com.pethub.entity.Product;
import com.pethub.entity.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    @Autowired
    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscountPrice(product.getDiscountPrice());
        response.setEffectivePrice(product.getEffectivePrice());
        response.setBrand(product.getBrand());
        response.setSku(product.getSku());
        response.setStockQuantity(product.getStockQuantity());
        response.setInStock(product.getStockQuantity() != null && product.getStockQuantity() > 0);

        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            response.setStockStatus("OUT_OF_STOCK");
        } else if (product.getStockQuantity() <= 5) {
            response.setStockStatus("LOW_STOCK");
        } else {
            response.setStockStatus("IN_STOCK");
        }

        response.setPetCategory(categoryMapper.toPetCategoryResponse(product.getPetCategory()));
        response.setProductCategory(categoryMapper.toProductCategoryResponse(product.getProductCategory()));
        response.setActive(product.isActive());
        response.setFeatured(product.isFeatured());
        response.setPrimaryImageUrl(product.getPrimaryImageUrl());

        if (product.getImages() != null) {
            response.setImages(product.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()));
        }

        response.setAverageRating(product.getAverageRating());
        response.setReviewCount(product.getReviewCount());
        response.setCreatedAt(product.getCreatedAt());

        return response;
    }

    public ProductImageResponse toImageResponse(ProductImage image) {
        if (image == null) return null;
        return new ProductImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.isPrimary(),
                image.getDisplayOrder()
        );
    }
}
