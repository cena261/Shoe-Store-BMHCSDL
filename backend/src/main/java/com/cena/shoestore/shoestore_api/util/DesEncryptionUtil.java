package com.cena.shoestore.shoestore_api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class DesEncryptionUtil {

    private static final String SECRET_KEY = "ShoeStoreAES128";
    private static final String ALGORITHM = "DES";
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            byte[] desKeyBytes = new byte[8];
            System.arraycopy(keyBytes, 0, desKeyBytes, 0, Math.min(keyBytes.length, 8));

            SecretKeySpec keySpec = new SecretKeySpec(desKeyBytes, ALGORITHM);

            byte[] iv = new byte[8];
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Error encrypting data with DES", e);
            return plainText;
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        if (isPlaintext(encryptedText)) {
            return encryptedText;
        }

        try {
            String cleanedText = encryptedText.replaceAll("\\s+", "");

            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            byte[] desKeyBytes = new byte[8];
            System.arraycopy(keyBytes, 0, desKeyBytes, 0, Math.min(keyBytes.length, 8));

            SecretKeySpec keySpec = new SecretKeySpec(desKeyBytes, ALGORITHM);

            byte[] iv = new byte[8];
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decoded = Base64.getDecoder().decode(cleanedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting phone number", e);
            return encryptedText;
        }
    }

    private boolean isPlaintext(String value) {
        if (value.length() <= 20 && value.matches("^[0-9+\\-\\s()]+$")) {
            return true;
        }
        return false;
    }
}
