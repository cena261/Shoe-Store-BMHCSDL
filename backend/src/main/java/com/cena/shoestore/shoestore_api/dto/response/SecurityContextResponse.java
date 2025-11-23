package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityContextResponse {
    // Application-level information
    String applicationUsername;
    Long userId;
    Instant lastLoginAt;
    String lastLoginIp;
    String lastLoginUserAgent;

    // Database-level information
    String dbUser;
    String sessionUser;
    String dbName;
    String dbHost;
    String dbSessionId;
    String clientIdentifier;

    // Tablespace and profile information
    String defaultTablespace;
    String temporaryTablespace;
    String profileName;
}
