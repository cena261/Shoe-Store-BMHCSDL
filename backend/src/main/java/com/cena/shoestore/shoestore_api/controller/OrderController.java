package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.CreateOrderRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateOrderStatusRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderResponse;
import com.cena.shoestore.shoestore_api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Order management endpoints for placing and viewing orders. Admin endpoints require Admin role and Bearer authentication.")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(
            summary = "Get all orders (Admin)",
            description = "Retrieve all orders in the system. Requires Admin role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            )
    })
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

    @GetMapping("/admin/orders/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Operation(
            summary = "Get order detail by ID (Admin)",
            description = "Retrieve detailed order information including items, variants, and shipping details. Requires Admin role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order detail retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByIdForAdmin(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        log.info("GET /orders/admin/orders/{} - Admin get order detail", id);

        OrderResponse order = orderService.getOrderByIdForAdmin(id);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order detail retrieved successfully")
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('Admin')")
    @Operation(
            summary = "Update order status (Admin)",
            description = "Update the status of an order. Valid statuses: PENDING, CONFIRMED, ON_DELIVERY, SUCCESS, CANCELLED, FAILED. Stock is deducted on CONFIRMED, restored on CANCELLED/FAILED. Requires Admin role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
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

    @DeleteMapping("/admin/orders/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Operation(
            summary = "Cancel order (Admin)",
            description = "Logically cancel an order by setting its status to CANCELLED. Restores stock if order was CONFIRMED or ON_DELIVERY. Does not delete the order record. Cannot cancel SUCCESS orders. Requires Admin role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cannot cancel SUCCESS orders"
            )
    })
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        log.info("DELETE /orders/admin/orders/{} - Admin cancel order", id);

        orderService.cancelOrder(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order cancelled successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
