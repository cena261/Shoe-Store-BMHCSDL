package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SizeOptionResponse {
    Long variantId;
    String sizeValue;
    Integer stockQty;
    Boolean isAvailable;
    BigDecimal priceOverride;
}
