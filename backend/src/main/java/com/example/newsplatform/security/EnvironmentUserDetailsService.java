package com.example.newsplatform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EnvironmentUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    public EnvironmentUserDetailsService(
            @Value("${admin.username:admin}") String adminUsername,
            @Value("${admin.password:admin}") String adminPassword,
            @Value("${editor.username:}") String editorUsername,
            @Value("${editor.password:}") String editorPassword,
            PasswordEncoder passwordEncoder) {
        
        // Admin user
        users.put(adminUsername, User.builder()
            .username(adminUsername)
            .password(passwordEncoder.encode(adminPassword))
            .roles("ADMIN")
            .build());
        
        // Editor user (if configured)
        if (!editorUsername.isBlank() && !editorPassword.isBlank()) {
            users.put(editorUsername, User.builder()
                .username(editorUsername)
                .password(passwordEncoder.encode(editorPassword))
                .roles("EDITOR")
                .build());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}