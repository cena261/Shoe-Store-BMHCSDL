package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ORDERITEMS")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERITEMID")
    Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERID", nullable = false)
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VARIANTID", nullable = false)
    ProductVariant productVariant;

    @Column(name = "QUANTITY", nullable = false)
    Integer quantity;

    @Column(name = "UNITPRICE", nullable = false, precision = 12, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "SUBTOTAL", nullable = false, precision = 18, scale = 2)
    BigDecimal subtotal;
}
