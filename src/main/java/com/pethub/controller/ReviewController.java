package com.pethub.controller;

import com.pethub.dto.request.ReviewRequest;
import com.pethub.dto.response.ApiResponse;
import com.pethub.dto.response.ReviewResponse;
import com.pethub.security.UserPrincipal;
import com.pethub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", reviews));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.addReview(userPrincipal.getId(), productId, request);
        return new ResponseEntity<>(ApiResponse.success("Review submitted successfully", review), HttpStatus.CREATED);
    }
}
