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
public class ProductDetailResponse {
    Long productId;
    String productName;
    String slug;
    String categoryName;
    String description;
    BigDecimal price;
    BigDecimal promotionPrice;
    BigDecimal displayPrice;
    BigDecimal rating;
    String selectedStyleColor;
    String selectedColorName;
    List<ColorOptionResponse> availableColors;
    List<String> images;
    List<SizeOptionResponse> availableSizes;
}
