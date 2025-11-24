package com.cena.shoestore.shoestore_api.controller;

import com.cena.shoestore.shoestore_api.dto.request.ForgotPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.ResetPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.VerifyResetTokenRequest;
import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import com.cena.shoestore.shoestore_api.dto.response.ForgotPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.ResetPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.VerifyTokenResponse;
import com.cena.shoestore.shoestore_api.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class PasswordResetController {

    PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset code",
            description = "Send a 6-digit reset code to the user's email. Code is encrypted with RSA asymmetric encryption and expires in 15 minutes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reset code sent successfully",
                    content = @Content(schema = @Schema(implementation = ForgotPasswordResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
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
    @Operation(
            summary = "Verify password reset code",
            description = "Verify that the reset code is valid and not expired. RSA decryption is used to validate the code."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyTokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token"
            )
    })
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
    @Operation(
            summary = "Reset password with verified code",
            description = "Reset password using a verified reset code. New password is hashed with BCrypt. Token is marked as used and cannot be reused."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully",
                    content = @Content(schema = @Schema(implementation = ResetPasswordResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid token or token already used"
            )
    })
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
