package com.itex.directcharge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    public static class Transaction {
        @NotBlank(message = "Merchant reference is required")
        private String merchantreference;
        private String callbackurl;
        @NotBlank(message = "Redirect URL is required")
        private String redirecturl;
        @NotBlank(message = "Authentication option is required")
        private String authoption;
        @NotBlank(message = "Payment method is required")
        private String paymentmethod;
        private String otp;
    }

    @Data
    public static class Order {
        @NotBlank(message = "Amount is required")
        private String amount;
        private String description;
        @NotBlank(message = "Currency is required")
        private String currency;
        @NotBlank(message = "Country code is required")
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
    public static class Card {
        @NotBlank(message = "Card number is required")
        private String number;
        @NotBlank(message = "Expiry month is required")
        private String expirymonth;
        @NotBlank(message = "Expiry year is required")
        private String expiryyear;
        @NotBlank(message = "CVV is required")
        private String cvv;
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
    public static class ChargeResponse {
        private Map<String, Object> transaction;
        private Map<String, Object> order;
        private Map<String, Object> source;
        private String code;
        private String message;
    }
}
