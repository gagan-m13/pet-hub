package com.pethub.controller;

import com.pethub.dto.request.CategoryRequest;
import com.pethub.dto.request.OrderStatusUpdateRequest;
import com.pethub.dto.request.ProductRequest;
import com.pethub.dto.response.*;
import com.pethub.entity.OrderStatus;
import com.pethub.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final FileStorageService fileStorageService;

    @Autowired
    public AdminController(AdminService adminService,
                           ProductService productService,
                           CategoryService categoryService,
                           OrderService orderService,
                           ReviewService reviewService,
                           FileStorageService fileStorageService) {
        this.adminService = adminService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.fileStorageService = fileStorageService;
    }

    // Dashboard Statistics
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics fetched successfully", stats));
    }

    // Product Management
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.success("Product created successfully", product), HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deactivated successfully", null));
    }

    // Image Upload
    @PostMapping(value = "/products/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", Map.of("imageUrl", fileUrl)));
    }

    @PostMapping("/products/{id}/images")
    public ResponseEntity<ApiResponse<ProductResponse>> addProductImage(
            @PathVariable Long id,
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        ProductResponse product = productService.addProductImage(id, imageUrl, isPrimary);
        return ResponseEntity.ok(ApiResponse.success("Image attached to product", product));
    }

    @DeleteMapping("/products/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Long imageId) {
        productService.deleteProductImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted", null));
    }

    @PutMapping("/products/{productId}/images/{imageId}/primary")
    public ResponseEntity<ApiResponse<ProductResponse>> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        ProductResponse product = productService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Primary image updated", product));
    }

    // Pet Categories CRUD
    @PostMapping("/pet-categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createPetCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createPetCategory(request);
        return new ResponseEntity<>(ApiResponse.success("Pet category created", category), HttpStatus.CREATED);
    }

    @PutMapping("/pet-categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updatePetCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updatePetCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pet category updated", category));
    }

    @DeleteMapping("/pet-categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePetCategory(@PathVariable Long id) {
        categoryService.deletePetCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Pet category deleted", null));
    }

    // Product Categories CRUD
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createProductCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createProductCategory(request);
        return new ResponseEntity<>(ApiResponse.success("Product category created", category), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateProductCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateProductCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product category updated", category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductCategory(@PathVariable Long id) {
        categoryService.deleteProductCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Product category deleted", null));
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PagedResponse<OrderResponse> orders = orderService.getAllOrdersAdmin(status, query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orders));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Order status updated to " + request.getStatus(), order));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileResponse>>> getAllUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PagedResponse<UserProfileResponse> users = adminService.getAllUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserProfileResponse>> toggleUserStatus(@PathVariable Long id) {
        UserProfileResponse user = adminService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("User account status updated", user));
    }

    // Review Moderation
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PagedResponse<ReviewResponse> reviews = reviewService.getAllReviewsAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", reviews));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
