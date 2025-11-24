package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.CreateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.request.UpdateAddressRequest;
import com.cena.shoestore.shoestore_api.dto.response.AddressResponse;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Addresses", description = "Shipping address management endpoints. Requires Bearer authentication. Phone numbers are encrypted with DES symmetric encryption (database-level).")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    AddressService addressService;

    @GetMapping
    @Operation(
            summary = "Get all shipping addresses for authenticated user",
            description = "Retrieve all shipping addresses for the current user. Phone numbers are automatically decrypted from DES encryption. Access is logged via FGA (Fine-Grained Auditing) policy."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Addresses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing Bearer token"
            )
    })
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
    @Operation(
            summary = "Create new shipping address",
            description = "Create a new shipping address for the authenticated user. Phone number is automatically encrypted with DES via database trigger before storage."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address created successfully",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing Bearer token"
            )
    })
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
    @Operation(
            summary = "Update existing shipping address",
            description = "Update shipping address details. User can only update their own addresses. Phone number is re-encrypted if changed."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - cannot update another user's address"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @Parameter(description = "Address ID") @PathVariable Long id,
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
    @Operation(
            summary = "Delete shipping address",
            description = "Delete a shipping address. User can only delete their own addresses."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - cannot delete another user's address"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(description = "Address ID") @PathVariable Long id) {
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
    @Operation(
            summary = "Set address as default shipping address",
            description = "Mark the specified address as the default shipping address for the user. Previous default is automatically unset."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Default address updated successfully",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - cannot modify another user's address"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            )
    })
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @Parameter(description = "Address ID") @PathVariable Long id) {
        log.info("PUT /addresses/{}/default - Set as default address", id);

        AddressResponse addressResponse = addressService.setDefaultAddress(id);

        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Default address updated successfully")
                .data(addressResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
