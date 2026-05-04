package com.itex.directcharge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/status")
@Tag(name = "Service Status", description = "Endpoints for checking the health and status of the service")
public class StatusController {

    @GetMapping
    @Operation(summary = "Get service status", description = "Returns the current status of the service, name, and timestamp.")
    public Map<String, Object> getStatus() {
        return Map.of(
            "status", "UP",
            "service", "ITEXPay Direct Charge Integration",
            "timestamp", LocalDateTime.now()
        );
    }
}
