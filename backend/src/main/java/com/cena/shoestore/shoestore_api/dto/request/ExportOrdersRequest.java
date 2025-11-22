package com.cena.shoestore.shoestore_api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportOrdersRequest {
    String orderStatus;
    String startDate;
    String endDate;
}
