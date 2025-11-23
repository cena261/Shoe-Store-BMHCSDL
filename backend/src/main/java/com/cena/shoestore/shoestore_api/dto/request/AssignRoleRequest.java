package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignRoleRequest {
    @NotBlank(message = "Role name is required")
    String roleName;
}
