package com.itex.directcharge.controller;

import com.itex.directcharge.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@WebMvcTest(StatusController.class)
@Import(SecurityConfig.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnStatusUp() throws Exception {
        mockMvc.perform(get("/api/v1/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("ITEXPay Direct Charge Integration")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
