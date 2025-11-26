package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.AddCartItemRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateCartItemRequest;
import com.cena.shoestore.shoestore_api.dto.response.CartItemResponse;
import com.cena.shoestore.shoestore_api.entity.Cart;
import com.cena.shoestore.shoestore_api.entity.ProductVariant;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.CartRepository;
import com.cena.shoestore.shoestore_api.repository.ProductVariantRepository;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    ProductVariantRepository productVariantRepository;
    UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public List<CartItemResponse> getCurrentUserCart() {
        User currentUser = getCurrentUser();
        log.info("Fetching cart for user: {}", currentUser.getEmail());

        List<Cart> cartItems = cartRepository.findByUser(currentUser);
        return cartItems.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemResponse addItem(AddCartItemRequest request) {
        User currentUser = getCurrentUser();
        log.info("Adding item to cart for user: {}", currentUser.getEmail());

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        if (variant.getIsActive() != 1) {
            throw new AppException(ErrorCode.VARIANT_INACTIVE);
        }

        if (variant.getStockQty() < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        Optional<Cart> existingCart = cartRepository.findByUserAndProductVariant(currentUser, variant);

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            int newQuantity = cart.getQuantity() + request.getQuantity();

            if (variant.getStockQty() < newQuantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            cart.setQuantity(newQuantity);
            log.info("Updated existing cart item quantity to: {}", newQuantity);
        } else {
            cart = Cart.builder()
                    .user(currentUser)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .addedAt(Instant.now())
                    .build();
            log.info("Created new cart item for variant: {}", variant.getVariantId());
        }

        cart = cartRepository.save(cart);
        return mapToCartItemResponse(cart);
    }

    @Transactional
    public CartItemResponse updateItem(Long cartId, UpdateCartItemRequest request) {
        User currentUser = getCurrentUser();
        log.info("Updating cart item ID: {} for user: {}", cartId, currentUser.getEmail());

        Cart cart = cartRepository.findByCartIdAndUser(cartId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        ProductVariant variant = cart.getProductVariant();
        if (variant.getStockQty() < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        cart.setQuantity(request.getQuantity());
        cart = cartRepository.save(cart);

        log.info("Cart item updated successfully: {}", cartId);
        return mapToCartItemResponse(cart);
    }

    @Transactional
    public void removeItem(Long cartId) {
        User currentUser = getCurrentUser();
        log.info("Removing cart item ID: {} for user: {}", cartId, currentUser.getEmail());

        Cart cart = cartRepository.findByCartIdAndUser(cartId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartRepository.delete(cart);
        log.info("Cart item deleted successfully: {}", cartId);
    }

    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        log.info("Clearing cart for user: {}", currentUser.getEmail());

        cartRepository.deleteByUser(currentUser);
        log.info("Cart cleared successfully");
    }

    private CartItemResponse mapToCartItemResponse(Cart cart) {
        ProductVariant variant = cart.getProductVariant();
        BigDecimal unitPrice = variant.getPriceOverride() != null
                ? variant.getPriceOverride()
                : variant.getProduct().getPrice();

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));

        String imageUrl = variant.getProduct().getImages().stream()
                .filter(img -> variant.getStyleColor().equals(img.getStyleColor())
                        && img.getDisplayOrder() != null
                        && img.getDisplayOrder() == 1)
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(null);

        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .variantId(variant.getVariantId())
                .productName(variant.getProduct().getProductName())
                .slug(variant.getProduct().getSlug())
                .category(variant.getProduct().getCategory().getCategoryName())
                .colorName(variant.getColorName())
                .sizeValue(variant.getSizeValue())
                .sku(variant.getSku())
                .imageUrl(imageUrl)
                .price(variant.getProduct().getPrice())
                .promotionPrice(variant.getProduct().getPromotionPrice())
                .unitPrice(unitPrice)
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .addedAt(cart.getAddedAt())
                .build();
    }
}
