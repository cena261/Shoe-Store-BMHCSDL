package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.request.UpdateOrderLabelRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.LabelSummaryResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderLabelResponse;
import com.cena.shoestore.shoestore_api.service.DbSessionContextService;
import com.cena.shoestore.shoestore_api.service.OrderLabelService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminOrderLabelController {

    OrderLabelService orderLabelService;
    DbSessionContextService dbSessionContextService;

    @PutMapping("/{orderId}/label")
    public ResponseEntity<ApiResponse<OrderLabelResponse>> updateOrderLabel(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderLabelRequest request) {
        log.info("PUT /admin/orders/{}/label - Update order label to {}", orderId, request.getLabel());

        orderLabelService.setOrderLabel(orderId, request.getLabel());

        OrderLabelResponse labelResponse = OrderLabelResponse.builder()
                .orderId(orderId)
                .securityLabel(request.getLabel())
                .message("Order label updated successfully")
                .build();

        ApiResponse<OrderLabelResponse> response = ApiResponse.<OrderLabelResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order security label updated successfully")
                .data(labelResponse)
                .build();

        log.info("Order {} label updated to {}", orderId, request.getLabel());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/labels/summary")
    public ResponseEntity<ApiResponse<LabelSummaryResponse>> getLabelSummary() {
        log.info("GET /admin/orders/labels/summary - Retrieving label summary");

        Map<String, Long> labelCounts = orderLabelService.getLabelSummary();
        Long totalOrders = labelCounts.values().stream().mapToLong(Long::longValue).sum();
        String currentContext = dbSessionContextService.getCurrentClientIdentifier();

        LabelSummaryResponse summaryResponse = LabelSummaryResponse.builder()
                .labelCounts(labelCounts)
                .totalOrders(totalOrders)
                .currentUserContext(currentContext != null ? currentContext : "NOT_SET")
                .build();

        ApiResponse<LabelSummaryResponse> response = ApiResponse.<LabelSummaryResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Label summary retrieved successfully")
                .data(summaryResponse)
                .build();

        log.info("Label summary retrieved - Total orders: {}, Labels: {}", totalOrders, labelCounts);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}/label")
    public ResponseEntity<ApiResponse<OrderLabelResponse>> getOrderLabel(@PathVariable Long orderId) {
        log.info("GET /admin/orders/{}/label - Retrieving order label", orderId);

        String label = orderLabelService.getOrderLabel(orderId);

        OrderLabelResponse labelResponse = OrderLabelResponse.builder()
                .orderId(orderId)
                .securityLabel(label)
                .message("Order label retrieved successfully")
                .build();

        ApiResponse<OrderLabelResponse> response = ApiResponse.<OrderLabelResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order label retrieved successfully")
                .data(labelResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
