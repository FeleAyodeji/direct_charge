package com.itex.directcharge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itex.directcharge.dto.ItexPayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItexPayClient {

    private final RestClient itexPayRestClient;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Value("${ITEXPAY_PUBLIC_KEY}")
    private String publicKey;

    @Value("${ITEXPAY_PRIVATE_KEY}")
    private String privateKey;

    @Value("${ITEXPAY_ENCRYPTION_KEY}")
    private String encryptedPublicKey;

    /**
     * Authenticates with ITEXPay to get an access token.
     */
    public ItexPayDto.AuthResponse authenticate() {
        log.info("Authenticating with ITEXPay Gateway");
        ItexPayDto.AuthRequest request = ItexPayDto.AuthRequest.builder()
                .publickey(publicKey)
                .privatekey(privateKey)
                .build();

        try {
            ResponseEntity<ItexPayDto.AuthResponse> response = itexPayRestClient.post()
                    .uri("/direct/transaction/authenticate")
                    .body(request)
                    .retrieve()
                    .toEntity(ItexPayDto.AuthResponse.class);

            log.info("Auth response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate with ITEXPay", e);
        }
    }

    /**
     * Initiates a charge by encrypting the payload and sending it to ITEXPay.
     */
    public ItexPayDto.ChargeResponse initiateCharge(ItexPayDto.TransactionPayload payload, String accessToken) {
        log.info("Initiating Direct Charge for reference: {}", payload.getTransaction().getMerchantreference());
        try {
            // 1. Serialize payload to JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // 2. Encrypt payload
            Map<String, String> encryptedData = encryptionService.encryptData(jsonPayload, encryptedPublicKey);

            ItexPayDto.EncryptedRequest request = ItexPayDto.EncryptedRequest.builder()
                    .ctx(encryptedData.get("ctx"))
                    .data(encryptedData.get("data"))
                    .build();

            // 3. Send encrypted request
            return itexPayRestClient.post()
                    .uri("/direct/transaction/charge")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(request)
                    .retrieve()
                    .body(ItexPayDto.ChargeResponse.class);

        } catch (Exception e) {
            log.error("Failed to initiate charge", e);
            throw new RuntimeException("Charge initiation failed", e);
        }
    }

    /**
     * Validates a PIN charge with an OTP.
     */
    public ItexPayDto.ChargeResponse validateCharge(String merchantReference, String otp, String accessToken) {
        log.info("Validating PIN Charge for reference: {}", merchantReference);
        try {
            // 1. Build raw request
            Map<String, Object> rawTransaction = Map.of("transaction", Map.of(
                    "merchantreference", merchantReference,
                    "otp", otp
            ));
            String jsonPayload = objectMapper.writeValueAsString(rawTransaction);

            // 2. Encrypt
            Map<String, String> encryptedData = encryptionService.encryptData(jsonPayload, encryptedPublicKey);

            ItexPayDto.EncryptedRequest request = ItexPayDto.EncryptedRequest.builder()
                    .ctx(encryptedData.get("ctx"))
                    .data(encryptedData.get("data"))
                    .build();

            // 3. Send
            return itexPayRestClient.post()
                    .uri("/direct/transaction/charge/validate")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(request)
                    .retrieve()
                    .body(ItexPayDto.ChargeResponse.class);

        } catch (Exception e) {
            log.error("Failed to validate charge", e);
            throw new RuntimeException("Charge validation failed", e);
        }
    }
}
