package com.pethub.mapper;

import com.pethub.dto.response.OrderItemResponse;
import com.pethub.dto.response.OrderResponse;
import com.pethub.entity.Order;
import com.pethub.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final UserMapper userMapper;

    @Autowired
    public OrderMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingFee(order.getShippingFee());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setItemCount(order.getItemCount());
        response.setCreatedAt(order.getCreatedAt());

        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserEmail(order.getUser().getEmail());
            response.setUserName(order.getUser().getFullName());
        }

        if (order.getShippingAddress() != null) {
            response.setShippingAddress(userMapper.toAddressResponse(order.getShippingAddress()));
        }

        if (order.getItems() != null) {
            response.setItems(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        }

        return response;
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) return null;

        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        if (item.getProduct() != null) {
            response.setProductId(item.getProduct().getId());
            response.setProductImageUrl(item.getProduct().getPrimaryImageUrl());
        }
        response.setProductName(item.getProductName());
        response.setProductSku(item.getProductSku());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());

        return response;
    }
}
