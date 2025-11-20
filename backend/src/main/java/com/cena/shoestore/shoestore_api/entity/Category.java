package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "CATEGORIES")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORYID")
    Long categoryId;

    @Column(name = "CATEGORYNAME", nullable = false, length = 50)
    String categoryName;

    @Column(name = "DESCRIPTION", length = 255)
    String description;

    @Column(name = "IMAGEURL", length = 500)
    String imageUrl;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    Set<Product> products;
}
