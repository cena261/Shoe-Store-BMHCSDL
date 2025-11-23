package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderLabelRequest {
    @NotBlank(message = "Label is required")
    @Pattern(regexp = "PUBLIC|CONFIDENTIAL|ADMIN_ONLY", message = "Label must be PUBLIC, CONFIDENTIAL, or ADMIN_ONLY")
    String label;
}
