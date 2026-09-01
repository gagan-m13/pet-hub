package com.pethub.service.impl;

import com.pethub.dto.response.DashboardStatsResponse;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.UserProfileResponse;
import com.pethub.entity.OrderStatus;
import com.pethub.entity.User;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.OrderMapper;
import com.pethub.mapper.ProductMapper;
import com.pethub.mapper.ReviewMapper;
import com.pethub.mapper.UserMapper;
import com.pethub.repository.OrderRepository;
import com.pethub.repository.ProductRepository;
import com.pethub.repository.ReviewRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ReviewMapper reviewMapper;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            ReviewRepository reviewRepository,
                            UserMapper userMapper,
                            OrderMapper orderMapper,
                            ProductMapper productMapper,
                            ReviewMapper reviewMapper) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalProducts(productRepository.count());
        stats.setTotalOrders(orderRepository.count());

        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        stats.setPendingOrders(orderRepository.countByStatus(OrderStatus.PLACED));
        stats.setLowStockProducts(productRepository.countByStockQuantityLessThanAndActiveTrue(10));

        stats.setRecentOrders(orderRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList()));

        stats.setLowStockProductList(productRepository.findAll().stream()
                .filter(p -> p.isActive() && p.getStockQuantity() < 10)
                .map(productMapper::toResponse)
                .collect(Collectors.toList()));

        stats.setRecentReviews(reviewRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList()));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserProfileResponse> getAllUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage;
        if (query != null && !query.trim().isEmpty()) {
            userPage = userRepository.searchUsers(query.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return new PagedResponse<>(
                userPage.getContent().stream().map(userMapper::toProfileResponse).collect(Collectors.toList()),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    @Transactional
    public UserProfileResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(!user.isEnabled());
        return userMapper.toProfileResponse(userRepository.save(user));
    }
}
