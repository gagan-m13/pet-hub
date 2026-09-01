package com.pethub.mapper;

import com.pethub.dto.response.CategoryResponse;
import com.pethub.entity.PetCategory;
import com.pethub.entity.ProductCategory;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toPetCategoryResponse(PetCategory category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl()
        );
    }

    public CategoryResponse toProductCategoryResponse(ProductCategory category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl()
        );
    }
}
