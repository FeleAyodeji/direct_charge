package com.itex.directcharge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private KeyPair keyPair;
    private String publicKeyBase64;

    @BeforeEach
    void setUp() throws Exception {
        encryptionService = new EncryptionService();

        // Generate a temporary RSA key pair for testing
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();
        publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    @Test
    void testEncryptData() throws Exception {
        String testData = "{\"test\": \"payload\"}";

        // 1. Run encryption
        Map<String, String> result = encryptionService.encryptData(testData, publicKeyBase64);

        assertNotNull(result.get("ctx"));
        assertNotNull(result.get("data"));

        // 2. Decrypt the AES key (ctx) using private key
        byte[] encryptedAesKey = Base64.getDecoder().decode(result.get("ctx"));
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decryptedAesKeyBase64 = rsaCipher.doFinal(encryptedAesKey);
        String aesKeyBase64 = new String(decryptedAesKeyBase64);

        // 3. Decrypt the data using the decrypted AES key
        byte[] aesKeyBytes = Base64.getDecoder().decode(aesKeyBase64);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyBytes, "AES");
        
        byte[] encryptedData = Base64.getDecoder().decode(result.get("data"));
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        
        byte[] decryptedData = aesCipher.doFinal(encryptedData);
        String decryptedString = new String(decryptedData);

        // 4. Verify
        assertEquals(testData, decryptedString);
    }
}
