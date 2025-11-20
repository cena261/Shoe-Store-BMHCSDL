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
        name = "REVIEWS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_REVIEWS_PRODUCT_USER", columnNames = {"PRODUCTID", "USERID"})
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEWID")
    Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTID", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID", nullable = false)
    User user;

    @Column(name = "RATING", nullable = false)
    Integer rating;

    @Lob
    @Column(name = "REVIEWCOMMENT")
    String reviewComment;

    @Column(name = "REVIEWDATE", nullable = false)
    Instant reviewDate;
}
