package com.pethub.mapper;

import com.pethub.dto.response.CartItemResponse;
import com.pethub.dto.response.CartResponse;
import com.pethub.entity.Cart;
import com.pethub.entity.CartItem;
import com.pethub.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        if (cart == null) return null;

        CartResponse response = new CartResponse();
        response.setId(cart.getId());

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQty = 0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemResponse = toItemResponse(item);
                itemResponses.add(itemResponse);
                subtotal = subtotal.add(itemResponse.getSubtotal());
                totalQty += itemResponse.getQuantity();
            }
        }

        response.setItems(itemResponses);
        response.setTotalQuantity(totalQty);
        response.setSubtotal(subtotal);

        // Free shipping if order > 999 else 99, 5% estimated tax
        BigDecimal shippingFee = (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(BigDecimal.valueOf(999)) < 0)
                ? BigDecimal.valueOf(99)
                : BigDecimal.ZERO;
        BigDecimal estimatedTax = subtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(shippingFee).add(estimatedTax);

        response.setShippingFee(shippingFee);
        response.setEstimatedTax(estimatedTax);
        response.setTotalAmount(totalAmount);

        return response;
    }

    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) return null;

        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setQuantity(item.getQuantity());

        Product product = item.getProduct();
        if (product != null) {
            response.setProductId(product.getId());
            response.setProductName(product.getName());
            response.setProductSlug(product.getSlug());
            response.setProductBrand(product.getBrand());
            response.setProductSku(product.getSku());
            response.setProductImageUrl(product.getPrimaryImageUrl());
            response.setUnitPrice(product.getEffectivePrice());
            response.setStockQuantity(product.getStockQuantity());
            response.setSubtotal(item.getSubtotal());
        }

        return response;
    }
}
