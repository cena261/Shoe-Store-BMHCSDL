package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlugAndIsActive(String slug, Integer isActive);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.variants v " +
           "WHERE p.isActive = 1 " +
           "AND (:search IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product = p AND LOWER(pv.styleColor) LIKE LOWER(CONCAT('%', :search, '%')))) " +
           "AND (:gender IS NULL OR c.categoryName IN :gender) " +
           "AND (:sizes IS NULL OR EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product = p AND pv.sizeValue IN :sizes AND pv.isActive = 1)) " +
           "AND (:minPrice IS NULL OR COALESCE(p.promotionPrice, p.price) >= :minPrice) " +
           "AND (:maxPrice IS NULL OR COALESCE(p.promotionPrice, p.price) <= :maxPrice)")
    Page<Product> findProductsWithFilters(
            @Param("search") String search,
            @Param("gender") List<String> gender,
            @Param("sizes") List<String> sizes,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.variants v " +
           "LEFT JOIN FETCH p.images i " +
           "WHERE p.slug = :slug AND p.isActive = 1")
    Optional<Product> findBySlugWithDetails(@Param("slug") String slug);
}
