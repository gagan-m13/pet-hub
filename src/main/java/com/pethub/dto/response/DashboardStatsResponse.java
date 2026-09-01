package com.pethub.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsResponse {

    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private long pendingOrders;
    private long lowStockProducts;

    private List<OrderResponse> recentOrders = new ArrayList<>();
    private List<ProductResponse> lowStockProductList = new ArrayList<>();
    private List<ReviewResponse> recentReviews = new ArrayList<>();

    public DashboardStatsResponse() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<OrderResponse> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<OrderResponse> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<ProductResponse> getLowStockProductList() {
        return lowStockProductList;
    }

    public void setLowStockProductList(List<ProductResponse> lowStockProductList) {
        this.lowStockProductList = lowStockProductList;
    }

    public List<ReviewResponse> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<ReviewResponse> recentReviews) {
        this.recentReviews = recentReviews;
    }
}
