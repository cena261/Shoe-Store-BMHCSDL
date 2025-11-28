package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageAdminRequest {

    private Long imageId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    private String styleColor;
}
