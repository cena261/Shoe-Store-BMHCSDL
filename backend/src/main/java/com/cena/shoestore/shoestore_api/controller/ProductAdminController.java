package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.ProductAdminCreateRequest;
import com.cena.shoestore.shoestore_api.dto.request.ProductAdminUpdateRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductAdminPageResponse;
import com.cena.shoestore.shoestore_api.dto.response.ProductAdminResponse;
import com.cena.shoestore.shoestore_api.service.ProductAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
@Tag(name = "Admin - Products", description = "Admin endpoints for managing products, variants, and images. Requires Admin role.")
public class ProductAdminController {

    ProductAdminService productAdminService;

    @GetMapping
    @Operation(
            summary = "Get product list for admin",
            description = "Browse all products (including inactive) with optional filters for search, category, and active status. Returns paginated results with basic product information."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductAdminPageResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProductAdminPageResponse>> getProductsForAdmin(
            @Parameter(description = "Search by product name") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by active status (1=active, 0=inactive)") @RequestParam(required = false) Integer isActive,
            @Parameter(description = "Page number (1-indexed)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Items per page (default: 15)") @RequestParam(defaultValue = "15") int pageSize
    ) {
        log.info("GET /admin/products - search: {}, categoryId: {}, isActive: {}, page: {}, pageSize: {}",
                search, categoryId, isActive, page, pageSize);

        ProductAdminPageResponse response = productAdminService.getProductsForAdmin(
                search, categoryId, isActive, page, pageSize
        );

        return ResponseEntity.ok(
                ApiResponse.<ProductAdminPageResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Products retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product detail by ID",
            description = "View detailed product information including all variants, images, and internal data. Includes inactive products."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product detail retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductAdminResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<ProductAdminResponse>> getProductByIdForAdmin(
            @Parameter(description = "Product ID") @PathVariable Long id
    ) {
        log.info("GET /admin/products/{}", id);

        ProductAdminResponse response = productAdminService.getProductByIdForAdmin(id);

        return ResponseEntity.ok(
                ApiResponse.<ProductAdminResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product detail retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/slug/{slug}")
    @Operation(
            summary = "Get product detail by slug",
            description = "View detailed product information by slug including all variants, images, and internal data. Includes inactive products."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product detail retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductAdminResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<ProductAdminResponse>> getProductBySlugForAdmin(
            @Parameter(description = "Product slug") @PathVariable String slug
    ) {
        log.info("GET /admin/products/slug/{}", slug);

        ProductAdminResponse response = productAdminService.getProductBySlugForAdmin(slug);

        return ResponseEntity.ok(
                ApiResponse.<ProductAdminResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product detail retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    @Operation(
            summary = "Create new product",
            description = "Create a new product with its basic data, variants, and images. The product will be active by default."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductAdminResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error"
            )
    })
    public ResponseEntity<ApiResponse<ProductAdminResponse>> createProduct(
            @Valid @RequestBody ProductAdminCreateRequest request
    ) {
        log.info("POST /admin/products - Creating product: {}", request.getProductName());

        ProductAdminResponse response = productAdminService.createProduct(request);

        return ResponseEntity.ok(
                ApiResponse.<ProductAdminResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update existing product",
            description = "Update an existing product including adding/removing/updating variants and images. All fields are optional for partial updates."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductAdminResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error"
            )
    })
    public ResponseEntity<ApiResponse<ProductAdminResponse>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductAdminUpdateRequest request
    ) {
        log.info("PUT /admin/products/{} - Updating product", id);

        ProductAdminResponse response = productAdminService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ProductAdminResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/active")
    @Operation(
            summary = "Activate or deactivate product",
            description = "Soft activate/deactivate a product by setting its isActive flag. Does not delete the product record."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product active status updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> setProductActive(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Active status (true=active, false=inactive)") @RequestParam boolean active
    ) {
        log.info("PATCH /admin/products/{}/active - Setting active status to: {}", id, active);

        productAdminService.setProductActive(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Product active status updated successfully")
                        .build()
        );
    }
}
