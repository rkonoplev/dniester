package com.example.phoebe.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for JSON array format.
 */
public class JsonArrayValidator implements ConstraintValidator<ValidJsonArray, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Allow null/empty values
        }
        
        try {
            JsonNode node = objectMapper.readTree(value);
            return node.isArray();
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}