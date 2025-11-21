package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.ForgotPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.ResetPasswordRequest;
import com.cena.shoestore.shoestore_api.dto.request.VerifyResetTokenRequest;
import com.cena.shoestore.shoestore_api.dto.response.ForgotPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.ResetPasswordResponse;
import com.cena.shoestore.shoestore_api.dto.response.VerifyTokenResponse;
import com.cena.shoestore.shoestore_api.entity.PasswordResetToken;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.PasswordResetTokenRepository;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import com.cena.shoestore.shoestore_api.util.RsaEncryptionUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetService {

    PasswordResetTokenRepository passwordResetTokenRepository;
    UserRepository userRepository;
    RsaEncryptionUtil rsaEncryptionUtil;
    PasswordEncoder passwordEncoder;

    private static final long TOKEN_EXPIRATION_MINUTES = 15;

    @Transactional
    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request) {
        log.info("Processing password reset request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsActive() != 1) {
            throw new AppException(ErrorCode.USER_INACTIVE);
        }

        passwordResetTokenRepository.deleteByEmail(request.getEmail());

        String rawToken = UUID.randomUUID().toString();
        String tokenPayload = String.format("%s:%s:%d",
                request.getEmail(),
                rawToken,
                Instant.now().toEpochMilli());

        String encryptedToken = rsaEncryptionUtil.encryptWithPublicKey(tokenPayload);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(request.getEmail())
                .code(encryptedToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_EXPIRATION_MINUTES * 60))
                .isUsed(0)
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token created and encrypted with RSA for email: {}", request.getEmail());

        return ForgotPasswordResponse.builder()
                .message("Password reset token sent successfully. Please check your email.")
                .email(request.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public VerifyTokenResponse verifyResetToken(VerifyResetTokenRequest request) {
        log.info("Verifying reset token for email: {}", request.getEmail());

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByEmailAndCodeAndIsUsed(request.getEmail(), request.getCode(), 0)
                .orElseThrow(() -> new AppException(ErrorCode.RESET_TOKEN_INVALID));

        if (Instant.now().isAfter(resetToken.getExpiresAt())) {
            log.warn("Reset token expired for email: {}", request.getEmail());
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        try {
            String decryptedPayload = rsaEncryptionUtil.decryptWithPrivateKey(resetToken.getCode());
            String[] parts = decryptedPayload.split(":");

            if (parts.length != 3 || !parts[0].equals(request.getEmail())) {
                log.error("RSA token verification failed for email: {}", request.getEmail());
                throw new AppException(ErrorCode.RSA_VERIFICATION_FAILED);
            }

            log.info("Reset token verified successfully for email: {}", request.getEmail());

            return VerifyTokenResponse.builder()
                    .valid(true)
                    .message("Reset token is valid")
                    .build();
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.RESET_TOKEN_INVALID) {
                log.error("RSA decryption failed for reset token");
                throw new AppException(ErrorCode.RSA_VERIFICATION_FAILED);
            }
            throw e;
        }
    }

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByEmailAndCodeAndIsUsed(request.getEmail(), request.getCode(), 0)
                .orElseThrow(() -> new AppException(ErrorCode.RESET_TOKEN_INVALID));

        if (Instant.now().isAfter(resetToken.getExpiresAt())) {
            log.warn("Reset token expired for email: {}", request.getEmail());
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        try {
            String decryptedPayload = rsaEncryptionUtil.decryptWithPrivateKey(resetToken.getCode());
            String[] parts = decryptedPayload.split(":");

            if (parts.length != 3 || !parts[0].equals(request.getEmail())) {
                log.error("RSA token verification failed during password reset for email: {}", request.getEmail());
                throw new AppException(ErrorCode.RSA_VERIFICATION_FAILED);
            }

            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            resetToken.setIsUsed(1);
            resetToken.setUsedAt(Instant.now());
            passwordResetTokenRepository.save(resetToken);

            log.info("Password reset completed successfully for email: {}", request.getEmail());

            return ResetPasswordResponse.builder()
                    .message("Password has been reset successfully")
                    .email(request.getEmail())
                    .build();
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.RESET_TOKEN_INVALID) {
                log.error("RSA decryption failed during password reset");
                throw new AppException(ErrorCode.RSA_VERIFICATION_FAILED);
            }
            throw e;
        }
    }

    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired password reset tokens");
        passwordResetTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
