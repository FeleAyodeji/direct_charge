package com.itex.directcharge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itex.directcharge.config.SecurityConfig;
import com.itex.directcharge.dto.ItexPayDto;
import com.itex.directcharge.service.DirectChargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DirectChargeController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.api-key=default-key")
class DirectChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DirectChargeService directChargeService;

    private static final String API_KEY = "default-key"; // From SecurityConfig default

    private ItexPayDto.TransactionPayload validPayload;
    private ItexPayDto.ChargeResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Setup valid payload
        validPayload = new ItexPayDto.TransactionPayload();
        
        ItexPayDto.Transaction transaction = new ItexPayDto.Transaction();
        transaction.setMerchantreference("REF123");
        transaction.setRedirecturl("https://example.com/callback");
        transaction.setAuthoption("PIN");
        transaction.setPaymentmethod("CARD");
        validPayload.setTransaction(transaction);

        ItexPayDto.Order order = new ItexPayDto.Order();
        order.setAmount("1000");
        order.setCurrency("NGN");
        order.setCountry("NG");
        validPayload.setOrder(order);

        ItexPayDto.Customer customer = new ItexPayDto.Customer();
        customer.setFirstname("John");
        customer.setLastname("Doe");
        customer.setEmail("john.doe@example.com");
        
        ItexPayDto.Source source = new ItexPayDto.Source();
        source.setCustomer(customer);
        validPayload.setSource(source);

        // Setup mock response
        mockResponse = new ItexPayDto.ChargeResponse();
        mockResponse.setCode("00");
        mockResponse.setMessage("Approved");
        mockResponse.setTransaction(Map.of("reference", "IBK123"));
    }

    @Test
    void initiateCharge_ShouldReturnOk_WhenValidRequest() throws Exception {
        when(directChargeService.processCharge(any(ItexPayDto.TransactionPayload.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/charge")
                .header("X-API-KEY", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Approved"));
    }

    @Test
    void initiateCharge_ShouldReturnForbidden_WhenApiKeyMissing() throws Exception {
        mockMvc.perform(post("/api/v1/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPayload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void initiateCharge_ShouldReturnBadRequest_WhenPayloadInvalid() throws Exception {
        ItexPayDto.TransactionPayload invalidPayload = new ItexPayDto.TransactionPayload();
        // Missing required fields...

        mockMvc.perform(post("/api/v1/charge")
                .header("X-API-KEY", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateCharge_ShouldReturnOk_WhenValidRequest() throws Exception {
        ItexPayDto.Transaction validateRequest = new ItexPayDto.Transaction();
        validateRequest.setMerchantreference("REF123");
        validateRequest.setOtp("123456");
        // Required fields for validation (though controller just uses merchantref and otp)
        validateRequest.setRedirecturl("http://example.com");
        validateRequest.setAuthoption("PIN");
        validateRequest.setPaymentmethod("CARD");

        when(directChargeService.validateCharge(anyString(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/charge/validate")
                .header("X-API-KEY", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"));
    }

    @Test
    void validateCharge_ShouldReturnForbidden_WhenApiKeyMissing() throws Exception {
        mockMvc.perform(post("/api/v1/charge/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }
}
