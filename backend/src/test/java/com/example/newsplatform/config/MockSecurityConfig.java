package com.example.newsplatform.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Mock security configuration for tests that provides all required security beans
 * without requiring external configuration.
 * Uses unique bean names to avoid conflicts with main SecurityConfig.
 */
@TestConfiguration
public class MockSecurityConfig {

    @Bean("testSecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests in tests
                );
        return http.build();
    }

    @Bean("testPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("testUserDetailsService")
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Create a test admin user for tests
        UserDetails user = User.builder()
                .username("testadmin")
                .password(passwordEncoder.encode("testpassword"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}