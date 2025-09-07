package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating taxonomy terms in admin panel.
 */
public class TermCreateRequest {

    @NotBlank(message = "Term name is required")
    @Size(max = 255, message = "Term name must not exceed 255 characters")
    private String name;

    @Size(max = 100, message = "Vocabulary must not exceed 100 characters")
    private String vocabulary;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVocabulary() { return vocabulary; }
    public void setVocabulary(String vocabulary) { this.vocabulary = vocabulary; }
}