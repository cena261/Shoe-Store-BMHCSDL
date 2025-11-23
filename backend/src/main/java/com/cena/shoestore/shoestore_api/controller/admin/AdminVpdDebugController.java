package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.VpdDebugResponse;
import com.cena.shoestore.shoestore_api.service.VpdDebugService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vpd")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminVpdDebugController {

    VpdDebugService vpdDebugService;

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<VpdDebugResponse>> getVpdDebugInfo() {
        log.info("GET /admin/vpd/orders - Retrieving VPD debug information");

        VpdDebugResponse debugInfo = vpdDebugService.getVpdDebugInfo();

        ApiResponse<VpdDebugResponse> response = ApiResponse.<VpdDebugResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("VPD debug information retrieved successfully")
                .data(debugInfo)
                .build();

        log.info("VPD debug info retrieved - Orders visible: {}", debugInfo.getTotalOrdersVisible());

        return ResponseEntity.ok(response);
    }
}
