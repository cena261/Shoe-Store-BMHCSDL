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
@Table(name = "SESSION_AUDIT")
public class SessionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SESSION_ID")
    Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    User user;

    @Column(name = "USERNAME", nullable = false, length = 255)
    String username;

    @Column(name = "LOGIN_AT", nullable = false)
    Instant loginAt;

    @Column(name = "IP_ADDRESS", length = 64)
    String ipAddress;

    @Column(name = "USER_AGENT", length = 512)
    String userAgent;

    @Column(name = "DB_USER", length = 128)
    String dbUser;

    @Column(name = "CLIENT_IDENTIFIER", length = 128)
    String clientIdentifier;
}
