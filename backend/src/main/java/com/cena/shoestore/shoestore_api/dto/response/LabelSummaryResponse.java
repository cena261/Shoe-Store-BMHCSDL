package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabelSummaryResponse {
    Map<String, Long> labelCounts;
    Long totalOrders;
    String currentUserContext;
}
