package com.cena.shoestore.shoestore_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageAdminResponse {

    private Long imageId;
    private Long productId;
    private String imageUrl;
    private Integer displayOrder;
    private String styleColor;
}
