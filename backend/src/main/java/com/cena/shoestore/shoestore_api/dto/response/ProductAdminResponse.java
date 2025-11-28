package com.cena.shoestore.shoestore_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {

    private Long productId;
    private String productName;
    private String slug;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal price;
    private BigDecimal promotionPrice;
    private Double rating;
    private Integer isActive;
    private LocalDateTime createdAt;
    private List<ProductVariantAdminResponse> variants;
    private List<ProductImageAdminResponse> images;
}
