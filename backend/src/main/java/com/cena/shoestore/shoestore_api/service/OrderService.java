package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.CreateOrderRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateOrderStatusRequest;
import com.cena.shoestore.shoestore_api.dto.response.OrderItemResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderResponse;
import com.cena.shoestore.shoestore_api.entity.*;
import com.cena.shoestore.shoestore_api.enums.OrderStatus;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.*;
import com.cena.shoestore.shoestore_api.util.DesEncryptionUtil;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    AddressRepository addressRepository;
    ProductVariantRepository productVariantRepository;
    DesEncryptionUtil desEncryptionUtil;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest request) {
        User currentUser = getCurrentUser();
        log.info("Placing order for user: {}", currentUser.getEmail());

        List<Cart> cartItems = cartRepository.findByUser(currentUser);
        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Address shippingAddress;
        if (request.getAddressId() != null) {
            shippingAddress = addressRepository.findByAddressIdAndUser(request.getAddressId(), currentUser)
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        } else {
            shippingAddress = addressRepository.findByUserAndIsDefault(currentUser, 1)
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cartItem : cartItems) {
            ProductVariant variant = cartItem.getProductVariant();

            if (variant.getIsActive() != 1) {
                throw new AppException(ErrorCode.VARIANT_INACTIVE);
            }

            if (variant.getStockQty() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            BigDecimal unitPrice = calculateUnitPrice(variant);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        Order order = Order.builder()
                .user(currentUser)
                .orderDate(Instant.now())
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING.name())
                .shippingName(shippingAddress.getFullName())
                .shippingPhone(shippingAddress.getPhone())
                .shippingTenDuong(shippingAddress.getTenDuong())
                .shippingXaQuan(shippingAddress.getXaQuan())
                .shippingTinhThanh(shippingAddress.getTinhThanh())
                .build();

        order = orderRepository.save(order);
        log.info("Order created with ID: {}", order.getOrderId());

        List<OrderItemResponse> orderItemResponses = new java.util.ArrayList<>();
        for (Cart cartItem : cartItems) {
            ProductVariant variant = cartItem.getProductVariant();
            BigDecimal unitPrice = calculateUnitPrice(variant);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            orderItem = orderItemRepository.save(orderItem);
            orderItemResponses.add(mapToOrderItemResponse(orderItem));
        }

        cartRepository.deleteByUser(currentUser);
        log.info("Cart cleared after order placement");

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingTenDuong(order.getShippingTenDuong())
                .shippingXaQuan(order.getShippingXaQuan())
                .shippingTinhThanh(order.getShippingTinhThanh())
                .orderItems(orderItemResponses)
                .build();
    }

    public List<OrderResponse> getCurrentUserOrders() {
        User currentUser = getCurrentUser();
        log.info("Fetching orders for user: {}", currentUser.getEmail());

        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(currentUser);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getCurrentUserOrderById(Long orderId) {
        User currentUser = getCurrentUser();
        log.info("Fetching order ID: {} for user: {}", orderId, currentUser.getEmail());

        Order order = orderRepository.findByOrderIdAndUser(orderId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrdersForAdmin() {
        log.info("Admin fetching all orders");

        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> {
                    Order orderWithItems = orderRepository.findByIdWithItems(order.getOrderId())
                            .orElse(order);
                    return mapToOrderResponse(orderWithItems);
                })
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        log.info("Admin fetching order ID: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return mapToOrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Admin cancelling order ID: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus currentStatus = OrderStatus.valueOf(order.getOrderStatus());

        if (currentStatus == OrderStatus.SUCCESS) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            log.warn("Order ID: {} is already cancelled", orderId);
            return;
        }

        if (currentStatus == OrderStatus.CONFIRMED || currentStatus == OrderStatus.ON_DELIVERY) {
            restoreStock(order);
        }

        order.setOrderStatus(OrderStatus.CANCELLED.name());
        orderRepository.save(order);

        log.info("Order ID: {} cancelled successfully", orderId);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("Updating order ID: {} to status: {}", orderId, request.getOrderStatus());

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getOrderStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        OrderStatus currentStatus = OrderStatus.valueOf(order.getOrderStatus());

        validateStatusTransition(currentStatus, newStatus);

        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED) {
            deductStock(order);
        } else if ((currentStatus == OrderStatus.CONFIRMED || currentStatus == OrderStatus.ON_DELIVERY)
                && (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.FAILED)) {
            restoreStock(order);
        }

        order.setOrderStatus(newStatus.name());
        order = orderRepository.save(order);

        log.info("Order status updated successfully");
        return mapToOrderResponse(order);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.SUCCESS || currentStatus == OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        if (currentStatus == OrderStatus.FAILED && newStatus != OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

    private void deductStock(Order order) {
        log.info("Deducting stock for order: {}", order.getOrderId());

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();

            if (variant.getStockQty() < item.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            variant.setStockQty(variant.getStockQty() - item.getQuantity());
            productVariantRepository.save(variant);
        }
    }

    private void restoreStock(Order order) {
        log.info("Restoring stock for order: {}", order.getOrderId());

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQty(variant.getStockQty() + item.getQuantity());
            productVariantRepository.save(variant);
        }
    }

    private BigDecimal calculateUnitPrice(ProductVariant variant) {
        if (variant.getPriceOverride() != null) {
            return variant.getPriceOverride();
        }

        Product product = variant.getProduct();
        if (product.getPromotionPrice() != null) {
            return product.getPromotionPrice();
        }

        return product.getPrice();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        String decryptedPhone = desEncryptionUtil.decrypt(order.getShippingPhone());
        log.debug("Phone number decrypted for order response");

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .shippingName(order.getShippingName())
                .shippingPhone(decryptedPhone)
                .shippingTenDuong(order.getShippingTenDuong())
                .shippingXaQuan(order.getShippingXaQuan())
                .shippingTinhThanh(order.getShippingTinhThanh())
                .orderItems(items)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        ProductVariant variant = item.getProductVariant();
        Product product = variant.getProduct();

        return OrderItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .variantId(variant.getVariantId())
                .productName(product.getProductName())
                .colorName(variant.getColorName())
                .sizeValue(variant.getSizeValue())
                .sku(variant.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
