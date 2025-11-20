package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCTID")
    Long productId;

    @Column(name = "PRODUCTNAME", nullable = false, length = 100)
    String productName;

    @Column(name = "SLUG", nullable = false, length = 150, unique = true)
    String slug;

    @Lob
    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @Column(name = "PROMOTIONPRICE", precision = 12, scale = 2)
    BigDecimal promotionPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORYID", nullable = false)
    Category category;

    @Column(name = "RATING", precision = 3, scale = 2)
    BigDecimal rating;

    @Column(name = "ISACTIVE", nullable = false)
    Integer isActive;

    @Column(name = "CREATEDAT", nullable = false)
    Instant createdAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    Set<ProductVariant> variants;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    Set<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    Set<Review> reviews;
}
