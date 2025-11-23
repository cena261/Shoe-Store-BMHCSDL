package com.cena.shoestore.shoestore_api.controller.admin;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.SecurityContextResponse;
import com.cena.shoestore.shoestore_api.service.SecuritySessionService;
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
@RequestMapping("/admin/security")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('Admin')")
public class AdminSecurityController {

    SecuritySessionService securitySessionService;

    @GetMapping("/current-session")
    public ResponseEntity<ApiResponse<SecurityContextResponse>> getCurrentSession() {
        log.info("GET /admin/security/current-session - Retrieving security context");

        SecurityContextResponse securityContext = securitySessionService.getCurrentSecurityContext();

        ApiResponse<SecurityContextResponse> response = ApiResponse.<SecurityContextResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Security context retrieved successfully")
                .data(securityContext)
                .build();

        log.info("Security context retrieved - DB User: {}, Tablespace: {}, Profile: {}",
                securityContext.getDbUser(),
                securityContext.getDefaultTablespace(),
                securityContext.getProfileName());

        return ResponseEntity.ok(response);
    }
}
