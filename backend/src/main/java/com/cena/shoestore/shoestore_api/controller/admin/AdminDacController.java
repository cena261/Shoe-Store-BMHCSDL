package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.DacAccessResponse;
import com.cena.shoestore.shoestore_api.service.DacService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for Discretionary Access Control (DAC) endpoints.
 *
 * Exposes database-level access control information including roles, privileges,
 * and restricted view data to demonstrate DAC implementation.
 *
 * Course Requirement #6: Business functions implementing DAC at the database level
 * through roles, privileges (GRANT/REVOKE), and restricted views.
 */
@RestController
@RequestMapping("/admin/security/dac")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminDacController {

    DacService dacService;

    @GetMapping
    public ResponseEntity<ApiResponse<DacAccessResponse>> getDacInfo() {
        log.info("GET /admin/security/dac - Retrieving DAC information");

        DacAccessResponse dacInfo = dacService.getDacInfo();

        ApiResponse<DacAccessResponse> response = ApiResponse.<DacAccessResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("DAC information retrieved successfully")
                .data(dacInfo)
                .build();

        log.info("DAC information retrieved - Roles: {}, Accessible Objects: {}",
                dacInfo.getDatabaseRoles().size(),
                dacInfo.getAccessibleObjects().size());

        return ResponseEntity.ok(response);
    }
}
