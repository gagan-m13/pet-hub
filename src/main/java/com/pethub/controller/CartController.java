package com.pethub.controller;

import com.pethub.dto.request.CartItemRequest;
import com.pethub.dto.response.ApiResponse;
import com.pethub.dto.response.CartResponse;
import com.pethub.security.UserPrincipal;
import com.pethub.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cart = cartService.getCartForUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart fetched successfully", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addItemToCart(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        CartResponse cart = cartService.updateItemQuantity(userPrincipal.getId(), itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeItemFromCart(userPrincipal.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }
}
