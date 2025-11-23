package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.LoginRequest;
import com.cena.shoestore.shoestore_api.dto.request.RegisterRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.AuthResponse;
import com.cena.shoestore.shoestore_api.service.AuthService;
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
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
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
