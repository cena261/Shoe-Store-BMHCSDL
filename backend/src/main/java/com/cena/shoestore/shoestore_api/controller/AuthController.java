package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.LoginRequest;
import com.cena.shoestore.shoestore_api.dto.request.RegisterRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.AuthResponse;
import com.cena.shoestore.shoestore_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication", description = "Public authentication endpoints for user registration and login. No Bearer token required.")
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user account",
            description = "Create a new user account with email, password, full name, and phone number. Password is hashed with BCrypt. Returns JWT token for immediate authentication."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or user already exists"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());

        AuthResponse authResponse = authService.register(request);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("User registered successfully")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user and receive JWT token",
            description = "Login with email and password. Returns JWT Bearer token for subsequent API calls. Sets Oracle CLIENT_IDENTIFIER for VPD/OLS security policies."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or inactive account"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("Login request received for email: {}", request.getEmail());

        AuthResponse authResponse = authService.login(request, httpRequest);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Login successful")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
