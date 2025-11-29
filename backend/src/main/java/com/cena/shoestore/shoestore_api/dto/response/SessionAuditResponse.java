package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionAuditResponse {

    Long sessionId;
    Long userId;
    String username;
    Instant loginAt;
    String ipAddress;
    String userAgent;
    String dbUser;
    String clientIdentifier;
}
