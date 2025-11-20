package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.CreateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.response.AddressResponse;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.service.AddressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressController {

    AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses() {
        log.info("GET /addresses - Fetch all addresses for current user");

        List<AddressResponse> addresses = addressService.getAllAddresses();

        ApiResponse<List<AddressResponse>> response = ApiResponse.<List<AddressResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Addresses retrieved successfully")
                .data(addresses)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody CreateAddressRequest request) {
        log.info("POST /addresses - Create new address");

        AddressResponse addressResponse = addressService.createAddress(request);

        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Address created successfully")
                .data(addressResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {
        log.info("PUT /addresses/{} - Update address", id);

        AddressResponse addressResponse = addressService.updateAddress(id, request);

        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Address updated successfully")
                .data(addressResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /addresses/{} - Delete address", id);

        addressService.deleteAddress(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .code("SUCCESS")
                .message("Address deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(@PathVariable Long id) {
        log.info("PUT /addresses/{}/default - Set address as default", id);

        AddressResponse addressResponse = addressService.setDefaultAddress(id);

        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Address set as default successfully")
                .data(addressResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
