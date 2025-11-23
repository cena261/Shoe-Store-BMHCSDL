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
@Table(name = "AUDIT_LOGS")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUDIT_ID")
    Long auditId;

    @Column(name = "EVENT_TIME", nullable = false)
    Instant eventTime;

    @Column(name = "USERNAME", length = 128)
    String username;

    @Column(name = "IP_ADDRESS", length = 128)
    String ipAddress;

    @Column(name = "HOST", length = 128)
    String host;

    @Column(name = "OBJECT_NAME", nullable = false, length = 128)
    String objectName;

    @Column(name = "ACTION", nullable = false, length = 20)
    String action;

    @Column(name = "ORDER_ID")
    Long orderId;

    @Column(name = "DETAILS", length = 2000)
    String details;
}
