package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.CategoryResponse;
import com.cena.shoestore.shoestore_api.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Categories", description = "Public product category endpoints for storefront navigation. No authentication required.")
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Get all product categories",
            description = "Retrieve all available product categories (e.g., Men, Women, Kids, Unisex). Used for navigation and filtering in the storefront."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("GET /categories");

        List<CategoryResponse> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Categories retrieved successfully")
                        .data(categories)
                        .build()
        );
    }
}
