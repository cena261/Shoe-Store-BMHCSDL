package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.ProductAdminCreateRequest;
import com.cena.shoestore.shoestore_api.dto.request.ProductAdminUpdateRequest;
import com.cena.shoestore.shoestore_api.dto.request.ProductImageAdminRequest;
import com.cena.shoestore.shoestore_api.dto.request.ProductVariantAdminRequest;
import com.cena.shoestore.shoestore_api.dto.response.*;
import com.cena.shoestore.shoestore_api.entity.Category;
import com.cena.shoestore.shoestore_api.entity.Product;
import com.cena.shoestore.shoestore_api.entity.ProductImage;
import com.cena.shoestore.shoestore_api.entity.ProductVariant;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.CategoryRepository;
import com.cena.shoestore.shoestore_api.repository.ProductImageRepository;
import com.cena.shoestore.shoestore_api.repository.ProductRepository;
import com.cena.shoestore.shoestore_api.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductAdminService {

    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;
    ProductImageRepository productImageRepository;
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductAdminPageResponse getProductsForAdmin(
            String search,
            Long categoryId,
            Integer isActive,
            int page,
            int pageSize
    ) {
        log.info("Admin: Fetching products with filters - search: {}, categoryId: {}, isActive: {}, page: {}, pageSize: {}",
                search, categoryId, isActive, page, pageSize);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> productPage;

        if (search != null && !search.isEmpty()) {
            productPage = productRepository.findAll(
                    (root, query, cb) -> {
                        var predicates = new ArrayList<>();

                        predicates.add(cb.like(cb.lower(root.get("productName")),
                                "%" + search.toLowerCase() + "%"));

                        if (categoryId != null) {
                            predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
                        }

                        if (isActive != null) {
                            predicates.add(cb.equal(root.get("isActive"), isActive));
                        }

                        return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                    },
                    pageable
            );
        } else {
            productPage = productRepository.findAll(
                    (root, query, cb) -> {
                        var predicates = new ArrayList<>();

                        if (categoryId != null) {
                            predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
                        }

                        if (isActive != null) {
                            predicates.add(cb.equal(root.get("isActive"), isActive));
                        }

                        if (predicates.isEmpty()) {
                            return cb.conjunction();
                        }

                        return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                    },
                    pageable
            );
        }

        List<ProductAdminResponse> products = productPage.getContent().stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());

        return ProductAdminPageResponse.builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .products(products)
                .build();
    }

    @Transactional(readOnly = true)
    public ProductAdminResponse getProductByIdForAdmin(Long productId) {
        log.info("Admin: Fetching product by ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return mapToAdminResponseWithDetails(product);
    }

    @Transactional(readOnly = true)
    public ProductAdminResponse getProductBySlugForAdmin(String slug) {
        log.info("Admin: Fetching product by slug: {}", slug);

        Product product = productRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("slug"), slug)
        ).stream().findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return mapToAdminResponseWithDetails(product);
    }

    @Transactional
    public ProductAdminResponse createProduct(ProductAdminCreateRequest request) {
        log.info("Admin: Creating new product: {}", request.getProductName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .productName(request.getProductName())
                .slug(request.getSlug())
                .category(category)
                .description(request.getDescription())
                .price(request.getPrice())
                .promotionPrice(request.getPromotionPrice())
                .isActive(1)
                .createdAt(Instant.now())
                .build();

        product = productRepository.save(product);

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (ProductVariantAdminRequest variantReq : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .sizeValue(variantReq.getSizeValue())
                        .colorName(variantReq.getColorName())
                        .sku(variantReq.getSku())
                        .styleColor(variantReq.getStyleColor())
                        .stockQty(variantReq.getStockQty())
                        .priceOverride(variantReq.getPriceOverride())
                        .isActive(variantReq.getIsActive() != null ? variantReq.getIsActive() : 1)
                        .build();
                productVariantRepository.save(variant);
            }
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (ProductImageAdminRequest imageReq : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageReq.getImageUrl())
                        .displayOrder(imageReq.getDisplayOrder())
                        .build();
                productImageRepository.save(image);
            }
        }

        return mapToAdminResponseWithDetails(product);
    }

    @Transactional
    public ProductAdminResponse updateProduct(Long productId, ProductAdminUpdateRequest request) {
        log.info("Admin: Updating product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getSlug() != null) {
            product.setSlug(request.getSlug());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getPromotionPrice() != null) {
            product.setPromotionPrice(request.getPromotionPrice());
        }

        product = productRepository.save(product);

        if (request.getVariants() != null) {
            for (ProductVariantAdminRequest variantReq : request.getVariants()) {
                if (variantReq.getVariantId() != null) {
                    ProductVariant variant = productVariantRepository.findById(variantReq.getVariantId())
                            .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

                    variant.setSizeValue(variantReq.getSizeValue());
                    variant.setColorName(variantReq.getColorName());
                    variant.setSku(variantReq.getSku());
                    variant.setStyleColor(variantReq.getStyleColor());
                    variant.setStockQty(variantReq.getStockQty());
                    variant.setPriceOverride(variantReq.getPriceOverride());
                    if (variantReq.getIsActive() != null) {
                        variant.setIsActive(variantReq.getIsActive());
                    }
                    productVariantRepository.save(variant);
                } else {
                    ProductVariant newVariant = ProductVariant.builder()
                            .product(product)
                            .sizeValue(variantReq.getSizeValue())
                            .colorName(variantReq.getColorName())
                            .sku(variantReq.getSku())
                            .styleColor(variantReq.getStyleColor())
                            .stockQty(variantReq.getStockQty())
                            .priceOverride(variantReq.getPriceOverride())
                            .isActive(variantReq.getIsActive() != null ? variantReq.getIsActive() : 1)
                            .build();
                    productVariantRepository.save(newVariant);
                }
            }
        }

        if (request.getImages() != null) {
            for (ProductImageAdminRequest imageReq : request.getImages()) {
                if (imageReq.getImageId() != null) {
                    ProductImage image = productImageRepository.findById(imageReq.getImageId())
                            .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

                    image.setImageUrl(imageReq.getImageUrl());
                    image.setDisplayOrder(imageReq.getDisplayOrder());
                    productImageRepository.save(image);
                } else {
                    ProductImage newImage = ProductImage.builder()
                            .product(product)
                            .imageUrl(imageReq.getImageUrl())
                            .displayOrder(imageReq.getDisplayOrder())
                            .build();
                    productImageRepository.save(newImage);
                }
            }
        }

        return mapToAdminResponseWithDetails(product);
    }

    @Transactional
    public void setProductActive(Long productId, boolean active) {
        log.info("Admin: Setting product ID {} active status to: {}", productId, active);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setIsActive(active ? 1 : 0);
        productRepository.save(product);
    }

    private ProductAdminResponse mapToAdminResponse(Product product) {
        return ProductAdminResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .slug(product.getSlug())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getCategoryName())
                .description(product.getDescription())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .rating(product.getRating() != null ? product.getRating().doubleValue() : null)
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    private ProductAdminResponse mapToAdminResponseWithDetails(Product product) {
        List<ProductVariantAdminResponse> variants = product.getVariants() != null
                ? product.getVariants().stream()
                    .map(v -> ProductVariantAdminResponse.builder()
                            .variantId(v.getVariantId())
                            .productId(v.getProduct().getProductId())
                            .sizeValue(v.getSizeValue())
                            .colorName(v.getColorName())
                            .sku(v.getSku())
                            .styleColor(v.getStyleColor())
                            .stockQty(v.getStockQty())
                            .priceOverride(v.getPriceOverride())
                            .isActive(v.getIsActive())
                            .build())
                    .collect(Collectors.toList())
                : new ArrayList<>();

        List<ProductImageAdminResponse> images = product.getImages() != null
                ? product.getImages().stream()
                    .map(i -> ProductImageAdminResponse.builder()
                            .imageId(i.getImageId())
                            .productId(i.getProduct().getProductId())
                            .imageUrl(i.getImageUrl())
                            .displayOrder(i.getDisplayOrder())
                            .build())
                    .collect(Collectors.toList())
                : new ArrayList<>();

        return ProductAdminResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .slug(product.getSlug())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getCategoryName())
                .description(product.getDescription())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .rating(product.getRating() != null ? product.getRating().doubleValue() : null)
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .variants(variants)
                .images(images)
                .build();
    }
}
