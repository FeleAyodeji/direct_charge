package com.itex.directcharge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItexPayDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthRequest {
        private String publickey;
        private String privatekey;
    }

    @Data
    public static class AuthResponse {
        private String access_token;
        private Long expires_in;
        private Map<String, Object> business;
        private String code;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EncryptedRequest {
        private String ctx;
        private String data;
    }

    @Data
    @Schema(description = "Request payload for initiating a transaction")
    public static class TransactionPayload {
        @Valid
        @NotNull(message = "Transaction details are required")
        private Transaction transaction;
        @Valid
        @NotNull(message = "Order details are required")
        private Order order;
        @Valid
        @NotNull(message = "Source details are required")
        private Source source;
        private Address billing;
        private Address shipping;
    }

    @Data
    @Schema(description = "Transaction specific details")
    public static class Transaction {
        @NotBlank(message = "Merchant reference is required")
        @Schema(example = "REF-123456", description = "Unique reference for the transaction")
        private String merchantreference;
        @Schema(example = "https://your-site.com/callback", description = "URL for server-to-server callback")
        private String callbackurl;
        @NotBlank(message = "Redirect URL is required")
        @Schema(example = "https://your-site.com/redirect", description = "URL to redirect the user after payment")
        private String redirecturl;
        @NotBlank(message = "Authentication option is required")
        @Schema(example = "PIN", description = "Authentication method (e.g., PIN, OTP)")
        private String authoption;
        @NotBlank(message = "Payment method is required")
        @Schema(example = "CARD", description = "Payment method used")
        private String paymentmethod;
        @Schema(example = "123456", description = "OTP code for validation (used in validate endpoint)")
        private String otp;
    }

    @Data
    @Schema(description = "Order details including amount and currency")
    public static class Order {
        @NotBlank(message = "Amount is required")
        @Schema(example = "100.00", description = "Amount to charge")
        private String amount;
        @Schema(example = "Payment for services", description = "Description of the order")
        private String description;
        @NotBlank(message = "Currency is required")
        @Schema(example = "NGN", description = "Currency code (ISO 4217)")
        private String currency;
        @NotBlank(message = "Country code is required")
        @Schema(example = "NG", description = "Country code (ISO 3166-1 alpha-2)")
        private String country;
    }

    @Data
    public static class Source {
        @Valid
        private Customer customer;
    }

    @Data
    public static class Customer {
        @NotBlank(message = "First name is required")
        private String firstname;
        @NotBlank(message = "Last name is required")
        private String lastname;
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        private String msisdn;
        @Valid
        private Card card;
        @Valid
        private Device device;
    }

    @Data
    @Schema(description = "Card details for payment")
    public static class Card {
        @NotBlank(message = "Card number is required")
        @Schema(example = "5399410000000000", description = "Credit/Debit card number")
        private String number;
        @NotBlank(message = "Expiry month is required")
        @Schema(example = "12", description = "Card expiry month (MM)")
        private String expirymonth;
        @NotBlank(message = "Expiry year is required")
        @Schema(example = "25", description = "Card expiry year (YY)")
        private String expiryyear;
        @NotBlank(message = "CVV is required")
        @Schema(example = "123", description = "Card security code")
        private String cvv;
        @Schema(example = "1111", description = "Card PIN (required for PIN auth)")
        private String pin;
    }

    @Data
    public static class Device {
        @NotBlank(message = "Device fingerprint is required")
        private String fingerprint;
        @NotBlank(message = "Device IP is required")
        private String ip;
    }

    @Data
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String postcode;
        private String country;
    }

    @Data
    @Schema(description = "Standard response for charge requests")
    public static class ChargeResponse {
        private Map<String, Object> transaction;
        private Map<String, Object> order;
        private Map<String, Object> source;
        @Schema(example = "00", description = "Response code")
        private String code;
        @Schema(example = "Approved", description = "Response message")
        private String message;
    }
}
