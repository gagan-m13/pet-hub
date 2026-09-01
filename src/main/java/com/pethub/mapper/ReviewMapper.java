package com.pethub.mapper;

import com.pethub.dto.response.ReviewResponse;
import com.pethub.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) return null;

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        if (review.getProduct() != null) {
            response.setProductId(review.getProduct().getId());
            response.setProductName(review.getProduct().getName());
        }

        if (review.getUser() != null) {
            response.setUserId(review.getUser().getId());
            response.setUserName(review.getUser().getFullName());
        }

        return response;
    }
}
