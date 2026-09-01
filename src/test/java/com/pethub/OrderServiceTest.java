package com.pethub;

import com.pethub.dto.request.CartItemRequest;
import com.pethub.dto.request.OrderRequest;
import com.pethub.dto.response.CartResponse;
import com.pethub.dto.response.OrderResponse;
import com.pethub.entity.Address;
import com.pethub.entity.OrderStatus;
import com.pethub.entity.Product;
import com.pethub.entity.User;
import com.pethub.repository.AddressRepository;
import com.pethub.repository.ProductRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.CartService;
import com.pethub.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testPlaceOrderAndInventoryDeduction() {
        User user = userRepository.findByEmail("customer@pethub.com").orElseThrow();
        Address address = addressRepository.findByUserId(user.getId()).get(0);
        Product product = productRepository.findAll().stream().filter(p -> p.getStockQuantity() > 10).findFirst().orElseThrow();

        int initialStock = product.getStockQuantity();

        // 1. Add item to cart
        cartService.addItemToCart(user.getId(), new CartItemRequest(product.getId(), 3));

        // 2. Place order
        OrderResponse order = orderService.placeOrder(user.getId(), new OrderRequest(address.getId(), "CASH_ON_DELIVERY"));
        assertNotNull(order);
        assertNotNull(order.getOrderNumber());
        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertEquals(3, order.getItemCount());

        // 3. Verify stock deduction
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(initialStock - 3, updatedProduct.getStockQuantity());

        // 4. Verify cart cleared
        CartResponse cart = cartService.getCartForUser(user.getId());
        assertTrue(cart.getItems().isEmpty());

        // 5. Verify snapshot price matches
        assertEquals(product.getEffectivePrice(), order.getItems().get(0).getUnitPrice());
    }

    @Test
    public void testAdminUpdateOrderStatus() {
        User user = userRepository.findByEmail("customer@pethub.com").orElseThrow();
        Address address = addressRepository.findByUserId(user.getId()).get(0);
        Product product = productRepository.findAll().stream().filter(p -> p.getStockQuantity() > 10).findFirst().orElseThrow();

        cartService.addItemToCart(user.getId(), new CartItemRequest(product.getId(), 1));
        OrderResponse order = orderService.placeOrder(user.getId(), new OrderRequest(address.getId(), "CASH_ON_DELIVERY"));

        OrderResponse updated = orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, updated.getStatus());
    }
}
