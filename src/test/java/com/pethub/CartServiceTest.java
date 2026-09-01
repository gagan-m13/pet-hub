package com.pethub;

import com.pethub.dto.request.CartItemRequest;
import com.pethub.dto.response.CartResponse;
import com.pethub.entity.Product;
import com.pethub.entity.User;
import com.pethub.exception.InsufficientStockException;
import com.pethub.repository.ProductRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testAddAndRemoveCartItem() {
        User user = userRepository.findByEmail("customer@pethub.com").orElseThrow();
        Product product = productRepository.findAll().stream().filter(p -> p.getStockQuantity() > 5).findFirst().orElseThrow();

        CartResponse cart = cartService.addItemToCart(user.getId(), new CartItemRequest(product.getId(), 2));
        assertNotNull(cart);
        assertTrue(cart.getTotalQuantity() >= 2);
        assertTrue(cart.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) > 0);

        Long itemId = cart.getItems().get(0).getId();
        CartResponse afterRemoval = cartService.removeItemFromCart(user.getId(), itemId);
        assertNotNull(afterRemoval);
    }

    @Test
    public void testExceedingStockThrowsException() {
        User user = userRepository.findByEmail("customer@pethub.com").orElseThrow();
        Product product = productRepository.findAll().stream().filter(p -> p.getStockQuantity() > 0).findFirst().orElseThrow();

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addItemToCart(user.getId(), new CartItemRequest(product.getId(), product.getStockQuantity() + 999));
        });
    }
}
