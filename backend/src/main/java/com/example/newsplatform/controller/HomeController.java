package com.example.newsplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    /**
     * Health-check endpoint to verify that the server is running.
     * Can be accessed without authentication.
     */
    @GetMapping("/")
    public String home() {
        return "✅ Server is running!";
    }
}
