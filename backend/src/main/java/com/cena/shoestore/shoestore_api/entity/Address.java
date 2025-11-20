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
@Table(name = "ADDRESSES")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESSID")
    Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID", nullable = false)
    User user;

    @Column(name = "FULLNAME", nullable = false, length = 100)
    String fullName;

    @Column(name = "PHONE", nullable = false, length = 20)
    String phone;

    @Column(name = "TENDUONG", nullable = false, length = 100)
    String tenDuong;

    @Column(name = "XAQUAN", nullable = false, length = 100)
    String xaQuan;

    @Column(name = "TINHTHANH", nullable = false, length = 100)
    String tinhThanh;

    @Column(name = "ISDEFAULT", nullable = false)
    Integer isDefault;
}
