package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long orderItemId;
    Long variantId;
    String productName;
    String colorName;
    String sizeValue;
    String sku;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
