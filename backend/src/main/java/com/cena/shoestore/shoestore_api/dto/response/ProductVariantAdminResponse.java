package com.cena.shoestore.shoestore_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantAdminResponse {

    private Long variantId;
    private Long productId;
    private String sizeValue;
    private String colorName;
    private String sku;
    private String styleColor;
    private Integer stockQty;
    private BigDecimal priceOverride;
    private Integer isActive;
}
