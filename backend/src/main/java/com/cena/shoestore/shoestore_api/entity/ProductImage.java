package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "PRODUCTIMAGES")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGEID")
    Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTID", nullable = false)
    Product product;

    @Column(name = "IMAGEURL", nullable = false, length = 500)
    String imageUrl;

    @Column(name = "DISPLAYORDER")
    Integer displayOrder;

    @Column(name = "STYLECOLOR", length = 50)
    String styleColor;
}
