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
@Table(name = "ORDEREXPORTS")
public class OrderExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPORT_ID")
    Long exportId;

    @Column(name = "CREATED_AT", nullable = false)
    Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false)
    User createdBy;

    @Column(name = "ENCRYPTED_KEY", nullable = false, length = 2000)
    String encryptedKey;

    @Lob
    @Column(name = "ENCRYPTED_DATA", nullable = false)
    byte[] encryptedData;
}
