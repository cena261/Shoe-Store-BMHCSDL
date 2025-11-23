package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolesResponse {
    Long userId;
    String email;
    String fullName;
    List<String> roles;
}
