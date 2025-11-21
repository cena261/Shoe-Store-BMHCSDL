package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Instant orderDate;
    String orderStatus;
    BigDecimal totalAmount;
    String shippingName;
    String shippingPhone;
    String shippingTenDuong;
    String shippingXaQuan;
    String shippingTinhThanh;
    List<OrderItemResponse> orderItems;
}
