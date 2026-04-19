package com.skaly.fashion_backend.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a new AES key
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Encrypt plaintext using AES-GCM
     */
    public static String encrypt(String plaintext, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length. Must be 256-bit (32 bytes)");
        }
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedBytes = cipher.doFinal(
            plaintext != null ? plaintext.getBytes(StandardCharsets.UTF_8) : new byte[0]
        );

        // Combine IV and encrypted data
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    /**
     * Decrypt ciphertext using AES-GCM
     */
    public static String decrypt(String ciphertext, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        byte[] decoded = Base64.getDecoder().decode(ciphertext);

        ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);

        // Extract encrypted data
        byte[] encryptedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Hash password using BCrypt (for password storage)
     */
    public static String hashPassword(String rawPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(rawPassword, org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
    }

    /**
     * Verify password against BCrypt hash
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
