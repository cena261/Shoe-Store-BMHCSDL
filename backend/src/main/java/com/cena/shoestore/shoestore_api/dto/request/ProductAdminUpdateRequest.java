package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminUpdateRequest {

    private String productName;

    private String slug;

    private Long categoryId;

    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private BigDecimal promotionPrice;

    private List<ProductVariantAdminRequest> variants;

    private List<ProductImageAdminRequest> images;
}
