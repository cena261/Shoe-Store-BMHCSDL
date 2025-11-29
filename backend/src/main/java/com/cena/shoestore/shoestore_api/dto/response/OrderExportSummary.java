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
public class OrderExportSummary {

    Long exportId;
    Instant createdAt;
    Long createdBy;
    String createdByEmail;
    Long encryptedDataSize;
}
