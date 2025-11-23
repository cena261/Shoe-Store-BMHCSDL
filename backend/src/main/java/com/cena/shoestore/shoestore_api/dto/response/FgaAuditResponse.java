package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FgaAuditResponse {
    Instant eventTime;
    String dbUser;
    String osUser;
    String host;
    String objectSchema;
    String objectName;
    String policyName;
    String clientIdentifier;
    String sqlText;
}
