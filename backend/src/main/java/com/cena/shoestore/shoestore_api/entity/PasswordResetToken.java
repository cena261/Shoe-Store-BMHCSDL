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
@Table(name = "PASSWORDRESETTOKENS")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKENID")
    Long tokenId;

    @Column(name = "EMAIL", nullable = false, length = 255)
    String email;

    @Column(name = "CODE", nullable = false, length = 6)
    String code;

    @Column(name = "CREATEDAT", nullable = false)
    Instant createdAt;

    @Column(name = "EXPIRESAT", nullable = false)
    Instant expiresAt;

    @Column(name = "ISUSED", nullable = false)
    Integer isUsed;

    @Column(name = "USEDAT")
    Instant usedAt;
}
