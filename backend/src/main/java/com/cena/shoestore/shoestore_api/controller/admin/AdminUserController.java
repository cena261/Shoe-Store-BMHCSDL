package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserAdminPageResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserResponse;
import com.cena.shoestore.shoestore_api.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Admin - Users", description = "Admin endpoints for managing users and their active status. Requires Admin role and Bearer authentication. For role management, see Admin - Role Management endpoints.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('Admin')")
public class AdminUserController {

    UserAdminService userAdminService;

    @GetMapping
    @Operation(
            summary = "Get user list for admin",
            description = "Browse all users (including inactive) with optional filters for search (email, fullName, phone) and active status. Returns paginated results."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserAdminPageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required"
            )
    })
    public ResponseEntity<ApiResponse<UserAdminPageResponse>> getUsersForAdmin(
            @Parameter(description = "Search by email, full name, or phone") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by active status (1=active, 0=inactive)") @RequestParam(required = false) Integer isActive,
            @Parameter(description = "Page number (1-indexed)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Items per page (default: 15)") @RequestParam(defaultValue = "15") int pageSize
    ) {
        log.info("GET /admin/users - search: {}, isActive: {}, page: {}, pageSize: {}",
                search, isActive, page, pageSize);

        UserAdminPageResponse response = userAdminService.getUsersForAdmin(
                search, isActive, page, pageSize
        );

        return ResponseEntity.ok(
                ApiResponse.<UserAdminPageResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("Users retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user detail by ID",
            description = "View detailed user information including assigned roles, phone, and account status. Includes inactive users."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User detail retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
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
    public ResponseEntity<ApiResponse<UserResponse>> getUserByIdForAdmin(
            @Parameter(description = "User ID") @PathVariable Long id
    ) {
        log.info("GET /admin/users/{}", id);

        UserResponse response = userAdminService.getUserByIdForAdmin(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("User detail retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/active")
    @Operation(
            summary = "Activate or deactivate user account",
            description = "Soft activate/deactivate a user account by setting its isActive flag. Does not delete the user record. Inactive users cannot log in."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User active status updated successfully"
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
    public ResponseEntity<ApiResponse<Void>> setUserActive(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Active status (true=active, false=inactive)") @RequestParam boolean active
    ) {
        log.info("PATCH /admin/users/{}/active - Setting active status to: {}", id, active);

        userAdminService.setUserActive(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status("success")
                        .code("SUCCESS")
                        .message("User active status updated successfully")
                        .build()
        );
    }
}
