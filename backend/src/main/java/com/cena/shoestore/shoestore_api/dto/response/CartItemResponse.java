package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long cartId;
    Long variantId;
    String productName;
    String colorName;
    String sizeValue;
    String sku;
    BigDecimal unitPrice;
    Integer quantity;
    BigDecimal subtotal;
    Instant addedAt;
}
