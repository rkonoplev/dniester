package com.example.phoebe.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void urlValidatorShouldAcceptValidUrls() {
        TestUrlClass testObj = new TestUrlClass("https://example.com");
        Set<ConstraintViolation<TestUrlClass>> violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void urlValidatorShouldRejectInvalidUrls() {
        TestUrlClass testObj = new TestUrlClass("invalid-url");
        Set<ConstraintViolation<TestUrlClass>> violations = validator.validate(testObj);
        assertFalse(violations.isEmpty());
    }

    @Test
    void jsonArrayValidatorShouldAcceptValidJsonArray() {
        TestJsonClass testObj = new TestJsonClass("[1,2,3]");
        Set<ConstraintViolation<TestJsonClass>> violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void jsonArrayValidatorShouldRejectInvalidJson() {
        TestJsonClass testObj = new TestJsonClass("invalid-json");
        Set<ConstraintViolation<TestJsonClass>> violations = validator.validate(testObj);
        assertFalse(violations.isEmpty());
    }

    static class TestUrlClass {
        @ValidUrl
        private String url;

        TestUrlClass(String url) {
            this.url = url;
        }
    }

    static class TestJsonClass {
        @ValidJsonArray
        private String jsonArray;

        TestJsonClass(String jsonArray) {
            this.jsonArray = jsonArray;
        }
    }
}