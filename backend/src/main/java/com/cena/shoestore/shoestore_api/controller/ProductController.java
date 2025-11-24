package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductDetailResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductPageResponse;
import com.cena.shoestore.shoestore_api.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {

    ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String sizes,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize
    ) {
        log.info("GET /products - search: {}, gender: {}, sizes: {}, minPrice: {}, maxPrice: {}, sortBy: {}, page: {}, pageSize: {}",
                search, gender, sizes, minPrice, maxPrice, sortBy, page, pageSize);

        ProductPageResponse response = productService.getProducts(
                search, gender, sizes, minPrice, maxPrice, sortBy, page, pageSize
        );

        return ResponseEntity.ok(
                ApiResponse.<ProductPageResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Products retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(
            @PathVariable String slug,
            @RequestParam(required = false) String styleColor
    ) {
        log.info("GET /products/{} - styleColor: {}", slug, styleColor);

        ProductDetailResponse response = productService.getProductDetail(slug, styleColor);

        return ResponseEntity.ok(
                ApiResponse.<ProductDetailResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product detail retrieved successfully")
                        .data(response)
                        .build()
        );
    }
}
