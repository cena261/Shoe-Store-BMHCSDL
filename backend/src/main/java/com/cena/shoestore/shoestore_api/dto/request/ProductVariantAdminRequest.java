package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantAdminRequest {

    private Long variantId;

    @NotBlank(message = "Size value is required")
    private String sizeValue;

    @NotBlank(message = "Color name is required")
    private String colorName;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Style color is required")
    private String styleColor;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQty;

    private BigDecimal priceOverride;

    private Integer isActive;
}
