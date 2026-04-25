package com.itex.directcharge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class EncryptionService {

    /**
     * Encrypts the transaction data following the ITEXPay standard.
     * Logic:
     * 1. Generate 16-byte AES key.
     * 2. Encrypt the Base64 representation of the AES key using the RSA public key.
     * 3. Encrypt the data using the AES key in ECB mode.
     *
     * @param transactionData   The JSON string to encrypt.
     * @param encryptedPublicKey Base64 encoded RSA public key.
     * @return Map containing "ctx" (encrypted key) and "data" (encrypted data).
     */
    public Map<String, String> encryptData(String transactionData, String encryptedPublicKey) {
        try {
            // 1. Generate AES key (128 bits)
            byte[] aesKeyBytes = new byte[16];
            new SecureRandom().nextBytes(aesKeyBytes);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyBytes, "AES");

            // 2. Convert AES key to Base64 (string representation to be RSA encrypted)
            String base64AesKey = Base64.getEncoder().encodeToString(aesKeyBytes);

            // 3. Decode RSA public key
            byte[] publicKeyBytes = Base64.getDecoder().decode(encryptedPublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);

            // 4. Encrypt AES key with RSA (OAEP Padding)
            // Node.js 'RSA_PKCS1_OAEP_PADDING' maps to 'RSA/ECB/OAEPWithSHA-1AndMGF1Padding' in Java
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(base64AesKey.getBytes(StandardCharsets.UTF_8));
            String ctx = Base64.getEncoder().encodeToString(encryptedAesKey);

            // 5. Encrypt data with AES (ECB mode)
            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] cipherText = aesCipher.doFinal(transactionData.getBytes(StandardCharsets.UTF_8));
            String data = Base64.getEncoder().encodeToString(cipherText);

            return Map.of("ctx", ctx, "data", data);

        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt transaction data", e);
        }
    }
}
