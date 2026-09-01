package com.pethub.controller;

import com.pethub.dto.request.OrderRequest;
import com.pethub.dto.response.ApiResponse;
import com.pethub.dto.response.OrderResponse;
import com.pethub.dto.response.PagedResponse;
import com.pethub.security.UserPrincipal;
import com.pethub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.placeOrder(userPrincipal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully", order), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<OrderResponse> orders = orderService.getUserOrders(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        OrderResponse order = orderService.getOrderDetails(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Order details fetched successfully", order));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        OrderResponse order = orderService.cancelOrder(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }
}
