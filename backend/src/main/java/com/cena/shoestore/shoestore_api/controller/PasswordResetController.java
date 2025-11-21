package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.ForgotPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.ResetPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.VerifyResetTokenRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ForgotPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.ResetPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.VerifyTokenResponse;
import com.cena.shoestore.shoestore_api.service.PasswordResetService;
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
public class PasswordResetController {

    PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request received for email: {}", request.getEmail());

        ForgotPasswordResponse forgotPasswordResponse = passwordResetService.requestPasswordReset(request);

        ApiResponse<ForgotPasswordResponse> response = ApiResponse.<ForgotPasswordResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Password reset token sent successfully")
                .data(forgotPasswordResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyResetToken(
            @Valid @RequestBody VerifyResetTokenRequest request) {
        log.info("Verify reset token request received for email: {}", request.getEmail());

        VerifyTokenResponse verifyTokenResponse = passwordResetService.verifyResetToken(request);

        ApiResponse<VerifyTokenResponse> response = ApiResponse.<VerifyTokenResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Reset token verified successfully")
                .data(verifyTokenResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request received for email: {}", request.getEmail());

        ResetPasswordResponse resetPasswordResponse = passwordResetService.resetPassword(request);

        ApiResponse<ResetPasswordResponse> response = ApiResponse.<ResetPasswordResponse>builder()
                .status("success")
                .code("SUCCESS")
                .message("Password reset successfully")
                .data(resetPasswordResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
