package com.cena.shoestore.shoestore_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAddressRequest {
    @NotBlank(message = "Full name is required")
    String fullName;

    @NotBlank(message = "Phone is required")
    String phone;

    @NotBlank(message = "Street is required")
    String tenDuong;

    @NotBlank(message = "District/Ward is required")
    String xaQuan;

    @NotBlank(message = "Province/City is required")
    String tinhThanh;

    Boolean isDefault;
}
