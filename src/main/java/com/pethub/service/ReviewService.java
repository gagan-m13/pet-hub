package com.pethub.service;

import com.pethub.dto.request.ReviewRequest;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(Long userId, Long productId, ReviewRequest request);
    List<ReviewResponse> getProductReviews(Long productId);
    PagedResponse<ReviewResponse> getAllReviewsAdmin(int page, int size);
    void deleteReview(Long reviewId);
}
