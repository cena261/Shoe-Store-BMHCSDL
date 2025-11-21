package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.CreateOrderRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateOrderStatusRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderResponse;
import com.cena.shoestore.shoestore_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @RequestBody CreateOrderRequest request) {
        log.info("POST /orders - Place order");

        OrderResponse order = orderService.placeOrder(request);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order placed successfully")
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getCurrentUserOrders() {
        log.info("GET /orders/my - Get current user orders");

        List<OrderResponse> orders = orderService.getCurrentUserOrders();

        ApiResponse<List<OrderResponse>> response = ApiResponse.<List<OrderResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Orders retrieved successfully")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getCurrentUserOrderById(
            @PathVariable Long id) {
        log.info("GET /orders/{} - Get order by ID", id);

        OrderResponse order = orderService.getCurrentUserOrderById(id);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order retrieved successfully")
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrdersForAdmin() {
        log.info("GET /orders/admin/orders - Admin get all orders");

        List<OrderResponse> orders = orderService.getAllOrdersForAdmin();

        ApiResponse<List<OrderResponse>> response = ApiResponse.<List<OrderResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("All orders retrieved successfully")
                .data(orders)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("PUT /orders/admin/orders/{}/status - Update order status", id);

        OrderResponse order = orderService.updateOrderStatus(id, request);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order status updated successfully")
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }
}
