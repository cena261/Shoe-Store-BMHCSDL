package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ProductAdminCreateRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private BigDecimal promotionPrice;

    private List<ProductVariantAdminRequest> variants;

    private List<ProductImageAdminRequest> images;
}
