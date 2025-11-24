package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.request.AssignRoleRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.RoleResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserRolesResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserSummaryResponse;
import com.cena.shoestore.shoestore_api.service.RoleManagementService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Admin - Role Management", description = "Admin endpoints for RBAC (Role-Based Access Control). Assign/revoke roles to users. Requires Admin role and Bearer authentication.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('Admin')")
public class AdminRoleController {

    RoleManagementService roleManagementService;

    @GetMapping("/users/{userId}/roles")
    @Operation(
            summary = "Get roles assigned to a specific user",
            description = "Retrieve all roles assigned to a user. Part of RBAC demonstration."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User roles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserRolesResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponse<UserRolesResponse>> getUserRoles(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("GET /admin/users/{}/roles - Get user roles", userId);

        UserRolesResponse userRoles = roleManagementService.getUserRoles(userId);

        ApiResponse<UserRolesResponse> response = ApiResponse.<UserRolesResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("User roles retrieved successfully")
                .data(userRoles)
                .build();

        log.info("User roles retrieved for user ID: {} - Roles: {}", userId, userRoles.getRoles());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<UserRolesResponse>> assignRole(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequest request) {
        log.info("POST /admin/users/{}/roles - Assign role {} to user", userId, request.getRoleName());

        roleManagementService.assignRole(userId, request.getRoleName());
        UserRolesResponse userRoles = roleManagementService.getUserRoles(userId);

        ApiResponse<UserRolesResponse> response = ApiResponse.<UserRolesResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Role assigned successfully")
                .data(userRoles)
                .build();

        log.info("Role {} assigned to user ID: {}", request.getRoleName(), userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse<UserRolesResponse>> revokeRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        log.info("DELETE /admin/users/{}/roles/{} - Revoke role from user", userId, roleName);

        roleManagementService.revokeRole(userId, roleName);
        UserRolesResponse userRoles = roleManagementService.getUserRoles(userId);

        ApiResponse<UserRolesResponse> response = ApiResponse.<UserRolesResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Role revoked successfully")
                .data(userRoles)
                .build();

        log.info("Role {} revoked from user ID: {}", roleName, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles/{roleName}/users")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByRole(@PathVariable String roleName) {
        log.info("GET /admin/roles/{}/users - Get users with role", roleName);

        List<UserSummaryResponse> users = roleManagementService.getUsersByRole(roleName);

        ApiResponse<List<UserSummaryResponse>> response = ApiResponse.<List<UserSummaryResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("Users with role " + roleName + " retrieved successfully")
                .data(users)
                .build();

        log.info("Retrieved {} users with role {}", users.size(), roleName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        log.info("GET /admin/roles - Get all roles");

        List<RoleResponse> roles = roleManagementService.getAllRoles();

        ApiResponse<List<RoleResponse>> response = ApiResponse.<List<RoleResponse>>builder()
                .status("success")
                .code("SUCCESS")
                .message("All roles retrieved successfully")
                .data(roles)
                .build();

        log.info("Retrieved {} roles", roles.size());

        return ResponseEntity.ok(response);
    }
}
