package com.itex.directcharge.service;

import com.itex.directcharge.dto.ItexPayDto;
import com.itex.directcharge.model.Transaction;
import com.itex.directcharge.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectChargeService {

    private final ItexPayClient itexPayClient;
    private final TransactionRepository transactionRepository;

    @Transactional
    public ItexPayDto.ChargeResponse processCharge(ItexPayDto.TransactionPayload payload) {
        log.info("Incoming charge payload: {}", payload);
        if (payload.getTransaction() == null) {
            throw new IllegalArgumentException("Transaction details are missing in payload");
        }
        String merchantRef = payload.getTransaction().getMerchantreference();
        log.info("Processing direct charge for reference: {}", merchantRef);

        // 1. Authenticate with ITEXPay
        ItexPayDto.AuthResponse authResponse = itexPayClient.authenticate();
        String accessToken = authResponse.getAccess_token();

        // 2. Save initial transaction record
        Transaction transaction = Transaction.builder()
                .merchantReference(merchantRef)
                .amount(payload.getOrder().getAmount())
                .currency(payload.getOrder().getCurrency())
                .authOption(payload.getTransaction().getAuthoption())
                .status("PENDING")
                .build();
        transactionRepository.save(transaction);

        // 3. Initiate Charge
        log.info("Initiating charge with ITEXPay using token: {}...", accessToken.substring(0, 5));
        ItexPayDto.ChargeResponse chargeResponse = itexPayClient.initiateCharge(payload, accessToken);
        log.info("ITEXPay Charge Response: {}", chargeResponse);

        // 4. Update transaction record with response details
        if (chargeResponse.getTransaction() != null) {
            transaction.setIbkReference((String) chargeResponse.getTransaction().get("reference"));
        }
        
        // In ITEXPay, 'approved' message or '00' code usually means success
        if ("00".equals(chargeResponse.getCode()) || "approved".equalsIgnoreCase(chargeResponse.getMessage())) {
            transaction.setStatus("APPROVED");
        } else {
            transaction.setStatus("FAILED");
            transaction.setMessage(chargeResponse.getMessage());
        }
        
        transactionRepository.save(transaction);
        return chargeResponse;
    }

    @Transactional
    public ItexPayDto.ChargeResponse validateCharge(String merchantReference, String otp) {
        log.info("Validating charge for reference: {}", merchantReference);

        Transaction transaction = transactionRepository.findByMerchantReference(merchantReference)
                .orElseThrow(() -> new RuntimeException("Transaction not found for reference: " + merchantReference));

        // 1. Authenticate
        ItexPayDto.AuthResponse authResponse = itexPayClient.authenticate();
        
        // 2. Validate
        ItexPayDto.ChargeResponse validationResponse = itexPayClient.validateCharge(merchantReference, otp, authResponse.getAccess_token());

        // 3. Update status
        if ("00".equals(validationResponse.getCode()) || "approved".equalsIgnoreCase(validationResponse.getMessage())) {
            transaction.setStatus("APPROVED");
        } else {
            transaction.setStatus("FAILED");
            transaction.setMessage(validationResponse.getMessage());
        }
        
        transactionRepository.save(transaction);
        return validationResponse;
    }
}
