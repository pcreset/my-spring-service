package com.myservice.app.controller;

import com.myservice.app.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Simple health and info endpoint.
 * Useful for load balancers and monitoring to check if the service is alive.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "my-spring-service"
        )));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> info() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "name", "my-spring-service",
                "version", "1.0.0",
                "description", "Spring Boot REST API Service"
        )));
    }

}
