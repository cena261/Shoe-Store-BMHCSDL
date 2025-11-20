package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.AddCartItemRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateCartItemRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.CartItemResponse;
import com.cena.shoestore.shoestore_api.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {

    CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCurrentUserCart() {
        log.info("GET /cart - Fetch cart items for current user");

        List<CartItemResponse> cartItems = cartService.getCurrentUserCart();

        ApiResponse<List<CartItemResponse>> response = ApiResponse.<List<CartItemResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Cart retrieved successfully")
                .data(cartItems)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("POST /cart - Add item to cart");

        CartItemResponse cartItem = cartService.addItem(request);

        ApiResponse<CartItemResponse> response = ApiResponse.<CartItemResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Item added to cart successfully")
                .data(cartItem)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateItem(
            @PathVariable Long cartId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        log.info("PUT /cart/{} - Update cart item", cartId);

        CartItemResponse cartItem = cartService.updateItem(cartId, request);

        ApiResponse<CartItemResponse> response = ApiResponse.<CartItemResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Cart item updated successfully")
                .data(cartItem)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long cartId) {
        log.info("DELETE /cart/{} - Remove cart item", cartId);

        cartService.removeItem(cartId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .code("SUCCESS")
                .message("Cart item removed successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        log.info("DELETE /cart - Clear cart");

        cartService.clearCart();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .code("SUCCESS")
                .message("Cart cleared successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
