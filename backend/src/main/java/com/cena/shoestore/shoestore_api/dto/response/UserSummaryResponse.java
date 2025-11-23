package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummaryResponse {
    Long userId;
    String email;
    String fullName;
    Integer isActive;
}
