package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PRODUCTVARIANTS")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VARIANTID")
    Long variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTID", nullable = false)
    Product product;

    @Column(name = "SIZEVALUE", nullable = false, length = 10)
    String sizeValue;

    @Column(name = "COLORNAME", nullable = false, length = 250)
    String colorName;

    @Column(name = "SKU", nullable = false, length = 250, unique = true)
    String sku;

    @Column(name = "STYLECOLOR", length = 50)
    String styleColor;

    @Column(name = "STOCKQTY", nullable = false)
    Integer stockQty;

    @Column(name = "PRICEOVERRIDE", precision = 12, scale = 2)
    BigDecimal priceOverride;

    @Column(name = "ISACTIVE", nullable = false)
    Integer isActive;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    Set<Cart> cartItems;
}
