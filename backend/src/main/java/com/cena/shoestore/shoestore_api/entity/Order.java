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
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERID")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID")
    User user;

    @Column(name = "ORDERDATE", nullable = false)
    Instant orderDate;

    @Column(name = "TOTALAMOUNT", nullable = false, precision = 12, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "ORDERSTATUS", length = 50)
    String orderStatus;

    @Column(name = "SHIPPINGNAME", nullable = false, length = 100)
    String shippingName;

    @Column(name = "SHIPPINGPHONE", nullable = false, length = 20)
    String shippingPhone;

    @Column(name = "SHIPPINGTENDUONG", nullable = false, length = 255)
    String shippingTenDuong;

    @Column(name = "SHIPPINGXAQUAN", nullable = false, length = 100)
    String shippingXaQuan;

    @Column(name = "SHIPPINGTINHTHANH", nullable = false, length = 100)
    String shippingTinhThanh;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    Set<OrderItem> orderItems;
}
