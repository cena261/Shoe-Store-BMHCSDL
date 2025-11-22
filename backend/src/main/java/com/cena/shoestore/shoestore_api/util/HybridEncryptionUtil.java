package com.cena.shoestore.shoestore_api.util;

import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HybridEncryptionUtil {

    RsaEncryptionUtil rsaEncryptionUtil;

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int AES_KEY_SIZE = 128;

    public HybridEncryptionResult encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new AppException(ErrorCode.HYBRID_ENCRYPTION_FAILED);
        }

        try {
            SecretKey aesKey = generateAesKey();
            String aesKeyBase64 = Base64.getEncoder().encodeToString(aesKey.getEncoded());

            byte[] encryptedData = encryptWithAes(plainText, aesKey);

            String encryptedAesKey = rsaEncryptionUtil.encryptWithPublicKey(aesKeyBase64);

            log.info("Hybrid encryption completed successfully. Data size: {} bytes, Encrypted size: {} bytes",
                    plainText.length(), encryptedData.length);

            return HybridEncryptionResult.builder()
                    .encryptedData(encryptedData)
                    .encryptedKey(encryptedAesKey)
                    .build();

        } catch (Exception e) {
            log.error("Hybrid encryption failed", e);
            throw new AppException(ErrorCode.HYBRID_ENCRYPTION_FAILED);
        }
    }

    public String decrypt(byte[] encryptedData, String encryptedAesKey) {
        if (encryptedData == null || encryptedAesKey == null || encryptedAesKey.isEmpty()) {
            throw new AppException(ErrorCode.HYBRID_DECRYPTION_FAILED);
        }

        try {
            String aesKeyBase64 = rsaEncryptionUtil.decryptWithPrivateKey(encryptedAesKey);

            byte[] aesKeyBytes = Base64.getDecoder().decode(aesKeyBase64);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, AES_ALGORITHM);

            String decryptedData = decryptWithAes(encryptedData, aesKey);

            log.info("Hybrid decryption completed successfully. Decrypted size: {} bytes", decryptedData.length());

            return decryptedData;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Hybrid decryption failed", e);
            throw new AppException(ErrorCode.HYBRID_DECRYPTION_FAILED);
        }
    }

    private SecretKey generateAesKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    private byte[] encryptWithAes(String plainText, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    private String decryptWithAes(byte[] encryptedData, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HybridEncryptionResult {
        byte[] encryptedData;
        String encryptedKey;
    }
}
