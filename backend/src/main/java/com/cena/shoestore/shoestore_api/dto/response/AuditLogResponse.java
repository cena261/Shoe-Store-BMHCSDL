package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogResponse {
    Long auditId;
    Instant eventTime;
    String username;
    String ipAddress;
    String host;
    String objectName;
    String action;
    Long orderId;
    String details;
}
