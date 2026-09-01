package com.pethub.service.impl;

import com.pethub.dto.request.CartItemRequest;
import com.pethub.dto.response.CartResponse;
import com.pethub.entity.Cart;
import com.pethub.entity.CartItem;
import com.pethub.entity.Product;
import com.pethub.entity.User;
import com.pethub.exception.BadRequestException;
import com.pethub.exception.InsufficientStockException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.CartMapper;
import com.pethub.repository.CartItemRepository;
import com.pethub.repository.CartRepository;
import com.pethub.repository.ProductRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartForUser(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("This product is currently inactive and cannot be added to cart.");
        }

        if (product.getStockQuantity() <= 0) {
            throw new InsufficientStockException("Product '" + product.getName() + "' is out of stock.");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        int newQuantity = request.getQuantity();
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Cannot add " + request.getQuantity() + " more items. Only " + product.getStockQuantity() + " available in stock.");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Cannot add " + newQuantity + " items. Only " + product.getStockQuantity() + " available in stock.");
            }
            CartItem newItem = new CartItem(cart, product, newQuantity);
            cartItemRepository.save(newItem);
        }

        Cart updatedCart = cartRepository.findById(cart.getId()).get();
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            if (quantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Requested quantity (" + quantity + ") exceeds available stock (" + product.getStockQuantity() + ").");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        Cart updatedCart = cartRepository.findById(cart.getId()).get();
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        cartItemRepository.delete(item);

        Cart updatedCart = cartRepository.findById(cart.getId()).get();
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
    }
}
