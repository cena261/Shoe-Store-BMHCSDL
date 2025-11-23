package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VpdDebugResponse {
    String currentClientIdentifier;
    String currentUser;
    Integer totalOrdersVisible;
    List<OrderSummary> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderSummary {
        Long orderId;
        Long userId;
        String userEmail;
        String orderDate;
        BigDecimal totalAmount;
        String orderStatus;
    }
}
