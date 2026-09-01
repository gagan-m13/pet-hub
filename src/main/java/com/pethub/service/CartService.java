package com.pethub.service;

import com.pethub.dto.request.CartItemRequest;
import com.pethub.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartForUser(Long userId);
    CartResponse addItemToCart(Long userId, CartItemRequest request);
    CartResponse updateItemQuantity(Long userId, Long itemId, int quantity);
    CartResponse removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
}
