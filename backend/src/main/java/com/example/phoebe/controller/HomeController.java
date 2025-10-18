package com.example.phoebe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// ✅ Swagger annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for home and health check endpoints.
 */
@RestController
@Tag(name = "Home API", description = "Service health-check and root endpoints")
public class HomeController {

    /**
     * Health-check endpoint to verify that the server is running.
     * Can be accessed without authentication.
     */
    @GetMapping("/")
    @Operation(summary = "Health-check", description = "Simple health-check endpoint to confirm the server is running.")
    public String home() {
        return "✅ Server is running!";
    }
}