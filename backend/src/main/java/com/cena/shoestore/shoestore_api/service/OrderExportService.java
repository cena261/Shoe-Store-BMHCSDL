package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.ExportOrdersRequest;
import com.cena.shoestore.shoestore_api.dto.response.ExportOrdersResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderExportDownloadResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderExportSummary;
import com.cena.shoestore.shoestore_api.entity.Order;
import com.cena.shoestore.shoestore_api.entity.OrderExport;
import com.cena.shoestore.shoestore_api.entity.OrderItem;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.OrderExportRepository;
import com.cena.shoestore.shoestore_api.repository.OrderRepository;
import com.cena.shoestore.shoestore_api.util.HybridEncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderExportService {

    OrderRepository orderRepository;
    OrderExportRepository orderExportRepository;
    HybridEncryptionUtil hybridEncryptionUtil;
    ObjectMapper objectMapper;
    com.cena.shoestore.shoestore_api.repository.UserRepository userRepository;

    @Transactional
    public ExportOrdersResponse exportOrders(ExportOrdersRequest request, String email) {
        log.info("Starting order export for admin user: {}", email);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findAll();
        log.info("Retrieved {} orders for export", orders.size());

        if (orders.isEmpty()) {
            log.warn("No orders found to export");
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        Map<String, Object> exportData = buildExportData(orders);

        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(exportData);
            log.info("Serialized order data to JSON. Size: {} characters", jsonData.length());
        } catch (Exception e) {
            log.error("Failed to serialize order data to JSON", e);
            throw new AppException(ErrorCode.ORDER_EXPORT_SERIALIZATION_FAILED);
        }

        HybridEncryptionUtil.HybridEncryptionResult encryptionResult =
                hybridEncryptionUtil.encrypt(jsonData);

        OrderExport orderExport = OrderExport.builder()
                .createdAt(Instant.now())
                .createdBy(currentUser)
                .encryptedKey(encryptionResult.getEncryptedKey())
                .encryptedData(encryptionResult.getEncryptedData())
                .build();

        OrderExport savedExport = orderExportRepository.save(orderExport);

        log.info("Order export saved successfully. Export ID: {}, Encrypted data size: {} bytes",
                savedExport.getExportId(), savedExport.getEncryptedData().length);

        return ExportOrdersResponse.builder()
                .exportId(savedExport.getExportId())
                .createdAt(savedExport.getCreatedAt())
                .message("Orders exported and encrypted successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public OrderExportDownloadResponse downloadExport(Long exportId, String email) {
        log.info("Downloading order export ID: {} for user: {}", exportId, email);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        OrderExport orderExport = orderExportRepository.findById(exportId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_EXPORT_NOT_FOUND));

        if (!orderExport.getCreatedBy().getUserId().equals(currentUser.getUserId())) {
            log.warn("User {} attempted to access export {} created by user {}",
                    currentUser.getUserId(), exportId, orderExport.getCreatedBy().getUserId());
            throw new AppException(ErrorCode.ORDER_EXPORT_ACCESS_DENIED);
        }

        String decryptedJson = hybridEncryptionUtil.decrypt(
                orderExport.getEncryptedData(),
                orderExport.getEncryptedKey()
        );

        log.info("Order export decrypted successfully. Export ID: {}, Decrypted size: {} characters",
                exportId, decryptedJson.length());

        return OrderExportDownloadResponse.builder()
                .exportId(exportId)
                .jsonData(decryptedJson)
                .message("Export downloaded and decrypted successfully")
                .build();
    }

    private Map<String, Object> buildExportData(List<Order> orders) {
        Map<String, Object> exportData = new HashMap<>();

        exportData.put("exportedAt", Instant.now().toString());
        exportData.put("totalOrders", orders.size());
        exportData.put("version", "1.0");

        List<Map<String, Object>> orderList = new ArrayList<>();

        for (Order order : orders) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", order.getOrderId());
            orderData.put("userId", order.getUser() != null ? order.getUser().getUserId() : null);
            orderData.put("userEmail", order.getUser() != null ? order.getUser().getEmail() : null);
            orderData.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : null);
            orderData.put("totalAmount", order.getTotalAmount());
            orderData.put("orderStatus", order.getOrderStatus());
            orderData.put("shippingName", order.getShippingName());
            orderData.put("shippingPhone", order.getShippingPhone());
            orderData.put("shippingTenDuong", order.getShippingTenDuong());
            orderData.put("shippingXaQuan", order.getShippingXaQuan());
            orderData.put("shippingTinhThanh", order.getShippingTinhThanh());

            List<Map<String, Object>> itemList = new ArrayList<>();
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("orderItemId", item.getOrderItemId());
                    itemData.put("variantId", item.getProductVariant() != null ?
                            item.getProductVariant().getVariantId() : null);
                    itemData.put("quantity", item.getQuantity());
                    itemData.put("unitPrice", item.getUnitPrice());
                    itemData.put("subtotal", item.getSubtotal());

                    if (item.getProductVariant() != null) {
                        itemData.put("sku", item.getProductVariant().getSku());
                        itemData.put("size", item.getProductVariant().getSizeValue());
                        itemData.put("color", item.getProductVariant().getColorName());

                        if (item.getProductVariant().getProduct() != null) {
                            itemData.put("productId", item.getProductVariant().getProduct().getProductId());
                            itemData.put("productName", item.getProductVariant().getProduct().getProductName());
                        }
                    }

                    itemList.add(itemData);
                }
            }

            orderData.put("items", itemList);
            orderData.put("itemCount", itemList.size());

            orderList.add(orderData);
        }

        exportData.put("orders", orderList);

        return exportData;
    }

    @Transactional(readOnly = true)
    public List<OrderExportSummary> getExportHistory(Integer limit, Long createdBy) {
        log.info("Retrieving export history - limit: {}, createdBy: {}", limit, createdBy);

        List<OrderExport> exports;

        if (createdBy != null) {
            exports = orderExportRepository.findRecentExportsByCreatedBy(createdBy, limit);
            log.info("Found {} exports for createdBy userId: {}", exports.size(), createdBy);
        } else {
            exports = orderExportRepository.findRecentExports(limit);
            log.info("Found {} recent exports", exports.size());
        }

        return exports.stream()
                .map(this::mapToOrderExportSummary)
                .collect(Collectors.toList());
    }

    private OrderExportSummary mapToOrderExportSummary(OrderExport export) {
        return OrderExportSummary.builder()
                .exportId(export.getExportId())
                .createdAt(export.getCreatedAt())
                .createdBy(export.getCreatedBy().getUserId())
                .createdByEmail(export.getCreatedBy().getEmail())
                .encryptedDataSize((long) export.getEncryptedData().length)
                .build();
    }
}
