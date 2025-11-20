package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "CART",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_CART_USER_VARIANT", columnNames = {"USERID", "VARIANTID"})
        }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARTID")
    Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VARIANTID", nullable = false)
    ProductVariant productVariant;

    @Column(name = "QUANTITY", nullable = false)
    Integer quantity;

    @Column(name = "ADDEDAT", nullable = false)
    Instant addedAt;
}
