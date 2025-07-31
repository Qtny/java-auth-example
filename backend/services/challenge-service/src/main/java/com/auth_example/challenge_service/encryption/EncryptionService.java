package com.auth_example.challenge_service.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class EncryptionService {

    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes

    private final SecretKey secretKey;

    public EncryptionService(@Value("${security.encryption.key}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("encryption key must be 256 bits (Base64 of 32 bytes");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encryptString(String subject) {
        log.info(":: encrypting...");

        try {
            // generate random 12 byte IV (nonce) per encryption
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom sc = new SecureRandom();
            sc.nextBytes(iv);

            // initialize cipher for aes/gcm/nopadding with generated iv and secret key
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // encrypt plain bytes
            byte[] cipherText = cipher.doFinal(subject.getBytes(StandardCharsets.UTF_8));

            // combine iv + cipher text to a single byte array
            byte[] encryptedData = new byte[IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, IV_LENGTH);
            System.arraycopy(cipherText, 0 , encryptedData, IV_LENGTH, cipherText.length);

            // encode combine byte array to URL-safe base64 string without padding
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("failed to encrypt data");
            throw new IllegalStateException("encryption failed", e);
        }
    }

    public String decryptString(String encryptedBase64) {
        try {
            // decode base64 url-safe string to bytes
            byte[] decoded =  Base64.getUrlDecoder().decode(encryptedBase64);

            // extract iv (first 12 bytes)
            byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);

            // extract cipher text (remaining bytes)
            byte[] cipherText = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

            // initialize cipher for aes/gcm/nopadding decryption with extracted iv and secret key
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            // decrypt cipher text to plain bytes
            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("failed to decrypt data");
            throw new IllegalStateException("decryption failed", e);
        }
    }
}
