package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.*;
import com.cena.shoestore.shoestore_api.entity.Product;
import com.cena.shoestore.shoestore_api.entity.ProductImage;
import com.cena.shoestore.shoestore_api.entity.ProductVariant;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductPageResponse getProducts(
            String search,
            String gender,
            String sizes,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            int page,
            int pageSize
    ) {
        log.info("Fetching products with filters - search: {}, gender: {}, sizes: {}, minPrice: {}, maxPrice: {}, sortBy: {}, page: {}, pageSize: {}",
                search, gender, sizes, minPrice, maxPrice, sortBy, page, pageSize);

        List<String> genderList = parseCommaSeparated(gender);
        List<String> sizeList = parseCommaSeparated(sizes);

        Sort sort = buildSort(sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> productPage = productRepository.findProductsWithFilters(
                search,
                genderList.isEmpty() ? null : genderList,
                sizeList.isEmpty() ? null : sizeList,
                minPrice,
                maxPrice,
                pageable
        );

        List<ProductListItemResponse> productResponses = productPage.getContent().stream()
                .map(this::mapToProductListItem)
                .collect(Collectors.toList());

        return ProductPageResponse.builder()
                .products(productResponses)
                .currentPage(page)
                .pageSize(pageSize)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(String slug, String styleColor) {
        log.info("Fetching product detail for slug: {}, styleColor: {}", slug, styleColor);

        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getIsActive() != 1) {
            throw new AppException(ErrorCode.PRODUCT_INACTIVE);
        }

        List<ProductVariant> activeVariants = product.getVariants().stream()
                .filter(v -> v.getIsActive() == 1)
                .sorted(Comparator.comparing(ProductVariant::getVariantId))
                .collect(Collectors.toList());

        if (activeVariants.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_INACTIVE);
        }

        String selectedStyle = determineSelectedStyle(styleColor, activeVariants);

        List<ProductVariant> variantsForSelectedStyle = activeVariants.stream()
                .filter(v -> selectedStyle.equals(v.getStyleColor()))
                .collect(Collectors.toList());

        String selectedColorName = variantsForSelectedStyle.isEmpty() ? "" :
                variantsForSelectedStyle.get(0).getColorName();

        List<ColorOptionResponse> availableColors = buildAvailableColors(activeVariants, product);

        List<String> images = buildImageList(product, selectedStyle);

        List<SizeOptionResponse> availableSizes = variantsForSelectedStyle.stream()
                .map(this::mapToSizeOption)
                .sorted(Comparator.comparing(SizeOptionResponse::getSizeValue))
                .collect(Collectors.toList());

        BigDecimal displayPrice = product.getPromotionPrice() != null ?
                product.getPromotionPrice() : product.getPrice();

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .slug(product.getSlug())
                .categoryName(product.getCategory().getCategoryName())
                .description(product.getDescription())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .displayPrice(displayPrice)
                .rating(product.getRating())
                .selectedStyleColor(selectedStyle)
                .selectedColorName(selectedColorName)
                .availableColors(availableColors)
                .images(images)
                .availableSizes(availableSizes)
                .build();
    }

    private ProductListItemResponse mapToProductListItem(Product product) {
        BigDecimal displayPrice = product.getPromotionPrice() != null ?
                product.getPromotionPrice() : product.getPrice();

        List<ColorVariantResponse> colorVariants = buildColorVariants(product);

        return ProductListItemResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .slug(product.getSlug())
                .categoryName(product.getCategory().getCategoryName())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .displayPrice(displayPrice)
                .colorVariants(colorVariants)
                .build();
    }

    private List<ColorVariantResponse> buildColorVariants(Product product) {
        Map<String, ProductVariant> styleColorMap = new LinkedHashMap<>();

        List<ProductVariant> activeVariants = product.getVariants().stream()
                .filter(v -> v.getIsActive() == 1)
                .sorted(Comparator.comparing(ProductVariant::getVariantId))
                .collect(Collectors.toList());

        for (ProductVariant variant : activeVariants) {
            String style = variant.getStyleColor();
            if (style != null && !styleColorMap.containsKey(style)) {
                styleColorMap.put(style, variant);
            }
        }

        return styleColorMap.values().stream()
                .map(variant -> {
                    String imageUrl = getMainImageUrlForStyle(product, variant.getStyleColor());
                    return ColorVariantResponse.builder()
                            .styleColor(variant.getStyleColor())
                            .colorName(variant.getColorName())
                            .mainImageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ColorOptionResponse> buildAvailableColors(List<ProductVariant> activeVariants, Product product) {
        Map<String, List<ProductVariant>> styleColorGroupMap = activeVariants.stream()
                .filter(v -> v.getStyleColor() != null)
                .collect(Collectors.groupingBy(ProductVariant::getStyleColor, LinkedHashMap::new, Collectors.toList()));

        return styleColorGroupMap.entrySet().stream()
                .map(entry -> {
                    String styleColor = entry.getKey();
                    List<ProductVariant> variants = entry.getValue();
                    boolean isAvailable = variants.stream().anyMatch(v -> v.getStockQty() > 0);
                    String colorName = variants.isEmpty() ? "" : variants.get(0).getColorName();
                    String thumbnailImage = getMainImageUrlForStyle(product, styleColor);

                    return ColorOptionResponse.builder()
                            .styleColor(styleColor)
                            .colorName(colorName)
                            .thumbnailImage(thumbnailImage)
                            .isAvailable(isAvailable)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String determineSelectedStyle(String requestedStyleColor, List<ProductVariant> activeVariants) {
        if (requestedStyleColor != null && !requestedStyleColor.isEmpty()) {
            boolean exists = activeVariants.stream()
                    .anyMatch(v -> requestedStyleColor.equals(v.getStyleColor()));
            if (exists) {
                return requestedStyleColor;
            }
        }

        Optional<ProductVariant> firstInStock = activeVariants.stream()
                .filter(v -> v.getStockQty() > 0)
                .sorted(Comparator.comparing(ProductVariant::getVariantId))
                .findFirst();

        if (firstInStock.isPresent()) {
            return firstInStock.get().getStyleColor();
        }

        return activeVariants.stream()
                .sorted(Comparator.comparing(ProductVariant::getVariantId))
                .findFirst()
                .map(ProductVariant::getStyleColor)
                .orElse("");
    }

    private List<String> buildImageList(Product product, String selectedStyle) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return new ArrayList<>();
        }

        return product.getImages().stream()
                .filter(img -> selectedStyle != null && selectedStyle.equals(img.getStyleColor()))
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProductImage::getImageId))
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }

    private String getMainImageUrl(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }

        return product.getImages().stream()
                .min(Comparator.comparing(ProductImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ProductImage::getImageId))
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }

    private String getMainImageUrlForStyle(Product product, String styleColor) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }

        return product.getImages().stream()
                .filter(img -> styleColor != null && styleColor.equals(img.getStyleColor()))
                .filter(img -> img.getDisplayOrder() != null && img.getDisplayOrder() == 1)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }

    private SizeOptionResponse mapToSizeOption(ProductVariant variant) {
        return SizeOptionResponse.builder()
                .variantId(variant.getVariantId())
                .sizeValue(variant.getSizeValue())
                .stockQty(variant.getStockQty())
                .isAvailable(variant.getStockQty() > 0)
                .priceOverride(variant.getPriceOverride())
                .build();
    }

    private List<String> parseCommaSeparated(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Sort buildSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "newest";
        }

        return switch (sortBy.toLowerCase()) {
            case "price-low" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-high" -> Sort.by(Sort.Direction.DESC, "price");
            case "name" -> Sort.by(Sort.Direction.ASC, "productName");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
