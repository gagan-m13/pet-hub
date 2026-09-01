package com.pethub.service.impl;

import com.pethub.dto.request.ReviewRequest;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.ReviewResponse;
import com.pethub.entity.Product;
import com.pethub.entity.Review;
import com.pethub.entity.User;
import com.pethub.exception.DuplicateResourceException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.ReviewMapper;
import com.pethub.repository.ProductRepository;
import com.pethub.repository.ReviewRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewResponse addReview(Long userId, Long productId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new DuplicateResourceException("You have already reviewed this product.");
        }

        Review review = new Review(product, user, request.getRating(), request.getComment().trim());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getAllReviewsAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Review> reviewPage = reviewRepository.findAllReviewsPaged(pageable);

        List<ReviewResponse> responses = reviewPage.getContent().stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}
