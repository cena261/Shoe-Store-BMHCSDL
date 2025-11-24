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
public class ProductListItemResponse {
    Long productId;
    String productName;
    String slug;
    String categoryName;
    BigDecimal price;
    BigDecimal promotionPrice;
    BigDecimal displayPrice;
    List<ColorVariantResponse> colorVariants;
}
