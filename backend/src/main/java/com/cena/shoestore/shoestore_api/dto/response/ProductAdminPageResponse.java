package com.cena.shoestore.shoestore_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminPageResponse {

    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<ProductAdminResponse> products;
}
