package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.request.ExportOrdersRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ExportOrdersResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderExportDownloadResponse;
import com.cena.shoestore.shoestore_api.dto.response.OrderExportSummary;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.service.OrderExportService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminOrderExportController {

    OrderExportService orderExportService;

    @PostMapping("/admin/orders/export")
    public ResponseEntity<ApiResponse<ExportOrdersResponse>> exportOrders(
            @Valid @RequestBody ExportOrdersRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("POST /admin/orders/export - Admin {} requesting order export", email);

        ExportOrdersResponse exportResponse = orderExportService.exportOrders(request, email);

        ApiResponse<ExportOrdersResponse> response = ApiResponse.<ExportOrdersResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Orders exported and encrypted successfully")
                .data(exportResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/orders/exports")
    public ResponseEntity<ApiResponse<List<OrderExportSummary>>> getExportHistory(
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(required = false) Long createdBy) {

        log.info("GET /admin/orders/exports - Retrieving export history (limit: {}, createdBy: {})",
                limit, createdBy);

        List<OrderExportSummary> exports = orderExportService.getExportHistory(limit, createdBy);

        ApiResponse<List<OrderExportSummary>> response = ApiResponse.<List<OrderExportSummary>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Export history retrieved successfully")
                .data(exports)
                .build();

        log.info("Retrieved {} export records", exports.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/orders/export/{exportId}/download")
    public ResponseEntity<ApiResponse<OrderExportDownloadResponse>> downloadExport(
            @PathVariable Long exportId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("GET /admin/orders/export/{}/download - Admin {} downloading export",
                exportId, email);

        OrderExportDownloadResponse downloadResponse =
                orderExportService.downloadExport(exportId, email);

        ApiResponse<OrderExportDownloadResponse> response =
                ApiResponse.<OrderExportDownloadResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Export downloaded and decrypted successfully")
                        .data(downloadResponse)
                        .build();

        return ResponseEntity.ok(response);
    }
}
