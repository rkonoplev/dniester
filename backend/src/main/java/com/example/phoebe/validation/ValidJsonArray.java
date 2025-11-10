package com.example.phoebe.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for JSON array format.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JsonArrayValidator.class)
public @interface ValidJsonArray {
    String message() default "Invalid JSON array format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}