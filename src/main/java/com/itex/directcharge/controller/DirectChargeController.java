package com.itex.directcharge.controller;

import com.itex.directcharge.dto.ItexPayDto;
import com.itex.directcharge.service.DirectChargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/charge")
@RequiredArgsConstructor
@Tag(name = "Direct Charge", description = "Endpoints for ITEXPay Direct Card Charges")
public class DirectChargeController {

    private final DirectChargeService directChargeService;

    @PostMapping
    @Operation(summary = "Initiate a direct card charge", description = "Authenticates, encrypts payload, and sends to ITEXPay charge endpoint.")
    public ResponseEntity<ItexPayDto.ChargeResponse> initiateCharge(@Valid @RequestBody ItexPayDto.TransactionPayload payload) {
        return ResponseEntity.ok(directChargeService.processCharge(payload));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a PIN charge", description = "Authorizes a transaction using an OTP for PIN-based authentication.")
    public ResponseEntity<ItexPayDto.ChargeResponse> validateCharge(@Valid @RequestBody ItexPayDto.Transaction payload) {
        return ResponseEntity.ok(directChargeService.validateCharge(payload.getMerchantreference(), payload.getOtp()));
    }
}
