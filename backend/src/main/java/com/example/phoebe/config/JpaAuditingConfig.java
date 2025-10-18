package com.example.phoebe.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA Auditing across the application.
 *
 * Notes:
 * - AuditorAware<String> returns the current user identifier when available.
 * - The default implementation returns empty to avoid coupling with security.
 * - If you integrate Spring Security, replace the bean implementation with a
 *   SecurityContext-based one (see example in the notes below).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    /**
     * Supplies the current auditor for @CreatedBy and @LastModifiedBy.
     * Returning empty is safe when you only use @CreatedDate and @LastModifiedDate.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return Optional::empty;
    }
}