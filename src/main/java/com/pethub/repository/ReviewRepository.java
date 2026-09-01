package com.pethub.repository;

import com.pethub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findAllReviewsPaged(Pageable pageable);

    List<Review> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingForProduct(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countReviewsForProduct(@Param("productId") Long productId);
}
