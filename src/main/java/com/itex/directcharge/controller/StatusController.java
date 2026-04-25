package com.itex.directcharge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    @GetMapping
    public Map<String, Object> getStatus() {
        return Map.of(
            "status", "UP",
            "service", "ITEXPay Direct Charge Integration",
            "timestamp", LocalDateTime.now()
        );
    }
}
