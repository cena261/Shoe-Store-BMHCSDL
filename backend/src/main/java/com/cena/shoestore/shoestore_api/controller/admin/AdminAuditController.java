package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.AuditLogResponse;
import com.cena.shoestore.shoestore_api.service.AuditLogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminAuditController {

    AuditLogService auditLogService;

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/logs - Retrieving {} recent audit logs", limit);

        List<AuditLogResponse> logs = auditLogService.getRecentAuditLogs(limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Audit logs retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} audit logs", logs.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/object/{objectName}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByObject(
            @PathVariable String objectName,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/logs/object/{} - Retrieving {} audit logs", objectName, limit);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByObjectName(objectName, limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Audit logs for " + objectName + " retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} audit logs for object: {}", logs.size(), objectName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getOrderAuditLogs(
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/orders - Retrieving {} order audit logs", limit);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByObjectName("ORDERS", limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Order audit logs retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} order audit logs", logs.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/user/{username}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/logs/user/{} - Retrieving {} audit logs", username, limit);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByUser(username, limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Audit logs for user " + username + " retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} audit logs for user: {}", logs.size(), username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/order/{orderId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("GET /admin/audit/logs/order/{} - Retrieving {} audit logs", orderId, limit);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByOrderId(orderId, limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Audit logs for order " + orderId + " retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} audit logs for order ID: {}", logs.size(), orderId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/search")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> searchAuditLogs(
            @RequestParam(required = false) String objectName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/logs/search - Filters: object={}, user={}, action={}, limit={}",
                objectName, username, action, limit);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByFilters(
                objectName, username, action, limit);

        ApiResponse<List<AuditLogResponse>> response = ApiResponse.<List<AuditLogResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Audit logs retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} audit logs with filters", logs.size());

        return ResponseEntity.ok(response);
    }
}
