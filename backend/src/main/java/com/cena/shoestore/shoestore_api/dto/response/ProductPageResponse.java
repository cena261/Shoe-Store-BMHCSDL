package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPageResponse {
    List<ProductListItemResponse> products;
    int currentPage;
    int pageSize;
    long totalElements;
    int totalPages;
}
