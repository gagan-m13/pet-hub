package com.pethub.service;

import com.pethub.dto.request.OrderRequest;
import com.pethub.dto.response.OrderResponse;
import com.pethub.dto.response.PagedResponse;
import com.pethub.entity.OrderStatus;

public interface OrderService {
    OrderResponse placeOrder(Long userId, OrderRequest request);
    PagedResponse<OrderResponse> getUserOrders(Long userId, int page, int size);
    OrderResponse getOrderDetails(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);

    // Admin
    PagedResponse<OrderResponse> getAllOrdersAdmin(OrderStatus status, String query, int page, int size);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}
