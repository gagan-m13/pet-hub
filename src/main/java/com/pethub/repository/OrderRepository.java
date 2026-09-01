package com.pethub.repository;

import com.pethub.entity.Order;
import com.pethub.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status) " +
           "AND (:query IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(o.user.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(o.user.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Order> searchOrdersAdmin(@Param("status") OrderStatus status, @Param("query") String query, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status <> 'CANCELLED'")
    BigDecimal calculateTotalRevenue();

    long countByStatus(OrderStatus status);

    List<Order> findTop5ByOrderByCreatedAtDesc();
}
