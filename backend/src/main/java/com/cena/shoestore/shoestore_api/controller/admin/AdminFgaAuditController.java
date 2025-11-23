package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.FgaAuditResponse;
import com.cena.shoestore.shoestore_api.service.FgaAuditService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/audit/fga")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminFgaAuditController {

    FgaAuditService fgaAuditService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<FgaAuditResponse>>> getUserFgaAuditLogs(
            @RequestParam(defaultValue = "50") int limit) {
        log.info("GET /admin/audit/fga/users - Retrieving {} FGA audit logs for USERS table", limit);

        List<FgaAuditResponse> logs = fgaAuditService.getRecentUserFgaEvents(limit);

        ApiResponse<List<FgaAuditResponse>> response = ApiResponse.<List<FgaAuditResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("FGA audit logs for USERS table retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} FGA audit logs for USERS table", logs.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/policy/{policyName}")
    public ResponseEntity<ApiResponse<List<FgaAuditResponse>>> getFgaAuditLogsByPolicy(
            @PathVariable String policyName,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("GET /admin/audit/fga/policy/{} - Retrieving {} FGA audit logs", policyName, limit);

        List<FgaAuditResponse> logs = fgaAuditService.getFgaEventsByPolicy(policyName, limit);

        ApiResponse<List<FgaAuditResponse>> response = ApiResponse.<List<FgaAuditResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("FGA audit logs for policy " + policyName + " retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} FGA audit logs for policy: {}", logs.size(), policyName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FgaAuditResponse>>> getAllFgaAuditLogs(
            @RequestParam(defaultValue = "100") int limit) {
        log.info("GET /admin/audit/fga/all - Retrieving {} FGA audit logs (all tables)", limit);

        List<FgaAuditResponse> logs = fgaAuditService.getAllRecentFgaEvents(limit);

        ApiResponse<List<FgaAuditResponse>> response = ApiResponse.<List<FgaAuditResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("All FGA audit logs retrieved successfully")
                .data(logs)
                .build();

        log.info("Retrieved {} FGA audit logs (all tables)", logs.size());

        return ResponseEntity.ok(response);
    }
}
