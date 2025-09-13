package com.example.newsplatform.config;

import com.example.newsplatform.filter.RateLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the News Platform application.
 * Provides:
 * - Basic Auth for admin endpoints
 * - Role-based access control for /api/admin/**
 * - CSRF disabled for API usage (with clear explanation why)
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private RateLimitFilter rateLimitFilter;

    // Inject admin credentials from application properties or ENV
    @Value("${admin.username:}")
    private String adminUsername;

    @Value("${admin.password:}")
    private String adminPassword;

    /**
     * Configure the HTTP security filter chain.
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain for the application
     * @throws Exception required by HttpSecurity contract
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF because this is a stateless REST API.
                // If in the future you serve HTML forms and session-based auth, enable CSRF here.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR")
                        .anyRequest().permitAll()
                )
                // Use HTTP Basic Auth for simplicity; consider JWT for production.
                .httpBasic(httpBasic -> {})
                // Add rate limiting filter before authentication
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Password encoder bean using BCrypt (recommended for production).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // UserDetailsService is now provided by EnvironmentUserDetailsService
}