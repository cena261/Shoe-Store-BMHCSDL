package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductDetailResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductPageResponse;
import com.cena.shoestore.shoestore_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "Public product browsing endpoints for the storefront. No authentication required.")
public class ProductController {

    ProductService productService;

    @GetMapping
    @Operation(
            summary = "Get product list with filtering and pagination",
            description = "Browse products with optional filters for search, gender/category, sizes, price range, and sorting. Returns paginated results with color variants for each product. Display price is promotion price if available, otherwise regular price."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductPageResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
            @Parameter(description = "Search by product name or style color") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by gender/category names (comma-separated, e.g., 'Men,Women,Kids,Unisex')") @RequestParam(required = false) String gender,
            @Parameter(description = "Filter by sizes (comma-separated, e.g., '38,39,40')") @RequestParam(required = false) String sizes,
            @Parameter(description = "Minimum display price filter") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum display price filter") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Sort option: 'newest' (default), 'price-low', 'price-high', 'name'") @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @Parameter(description = "Page number (1-indexed)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Items per page (default: 15)") @RequestParam(defaultValue = "15") int pageSize
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
    @Operation(
            summary = "Get product detail by slug",
            description = "View detailed product information including available colors, sizes, stock quantities, and images. Optionally specify a styleColor to view that specific color variant. If not specified, defaults to first available color with stock."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product detail retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found or inactive"
            )
    })
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(
            @Parameter(description = "Product slug (URL-friendly identifier)") @PathVariable String slug,
            @Parameter(description = "Optional style/color code to view specific variant") @RequestParam(required = false) String styleColor
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
