# Role-Based Security Implementation Guide

This document outlines the **correct** implementation requirements for ADMIN and EDITOR role security in Phoebe CMS.

## Role Definitions

### ADMIN Role
- **Full system access** - can perform any operation
- **All content management** - create, read, update, delete any article
- **User management** - manage user profiles and roles
- **System configuration** - manage terms, categories, settings
- **Bulk operations** - system-wide bulk actions

### EDITOR Role
- **Own content only** - can only manage articles they authored
- **Content creation** - can create new articles (becomes author)
- **Author-restricted operations** - edit/delete only where `author_id` matches user ID
- **Publication control** - publish/unpublish own articles only
- **Read-only access** - can view but not modify others' content

## Security Implementation

### 1. Service Layer Security (CORRECT IMPLEMENTATION)

#### NewsService Methods - Proper Authorization

```java
// ADMIN: Full access, EDITOR: Own content only
@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication auth) {
    // Security check handled by @PreAuthorize
}

@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")  
public void delete(Long id, Authentication auth) {
    // Security check handled by @PreAuthorize
}

// ADMIN: All content, EDITOR: Own content only
@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
public Page<NewsDto> findAll(Pageable pageable, Authentication auth) {
    // Manual filtering inside method based on role
    if (hasAdminRole(auth)) {
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    } else {
        Long authorId = getCurrentUserId(auth);
        return newsRepository.findByAuthorId(authorId, pageable).map(newsMapper::toDto);
    }
}
```
### 2. Authorization Service Methods
#### NewsServiceImpl Authorization Methods
```java
public boolean canAccessNews(Long newsId, Authentication authentication) {
    if (hasAdminRole(authentication)) {
        return true; // ADMIN can access any news
    }
    
    if (hasEditorRole(authentication)) {
        return isAuthor(newsId, authentication); // EDITOR can only access own news
    }
    
    return false;
}

public boolean isAuthor(Long newsId, Authentication authentication) {
    Long currentUserId = getCurrentUserId(authentication);
    return newsRepository.existsByIdAndAuthorId(newsId, currentUserId);
}

public boolean hasAdminRole(Authentication authentication) {
    return hasAuthority(authentication, RoleConstants.ROLE_ADMIN);
}

public boolean hasEditorRole(Authentication authentication) {
    return hasAuthority(authentication, RoleConstants.ROLE_EDITOR);
}
```
### 3. Repository Layer Enhancements
#### NewsRepository - Author Verification
```java
// Check if user is author of specific article
boolean existsByIdAndAuthorId(Long id, Long authorId);

// For EDITOR role - find only own content
Page<News> findByAuthorId(Long authorId, Pageable pageable);
```
### 4. Security Configuration
#### SecurityConfig - Proper Role Setup
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ← ENABLES @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "EDITOR")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```
### Testing Strategy
#### Unit Tests for Authorization
##### Testing ADMIN vs EDITOR Access
```java
@Test
void findAll_AdminRole_ShouldReturnAllNews() {
    // Given admin authentication
    Authentication adminAuth = createAdminAuthentication();
    
    // When
    var result = newsService.findAll(pageable, adminAuth);
    
    // Then - should return all news
    assertEquals(3, result.getContent().size());
}

@Test
void findAll_EditorRole_ShouldReturnOnlyOwnNews() {
    // Given editor authentication  
    Authentication editorAuth = createEditorAuthentication();
    
    // When
    var result = newsService.findAll(pageable, editorAuth);
    
    // Then - should return only editor's news
    assertEquals(1, result.getContent().size());
}
```
##### Testing Author-Based Security
```java
@Test
void update_EditorRole_ShouldAllowUpdatingOwnNews() {
    // Editor tries to update their own news - should succeed
    assertDoesNotThrow(() -> newsService.update(2L, request, editorAuth));
}

@Test 
void update_EditorRole_ShouldDenyUpdatingOthersNews() {
    // Editor tries to update other's news - should fail
    assertThrows(AccessDeniedException.class, 
        () -> newsService.update(1L, request, editorAuth));
}
```
#### Common Patterns
##### 1. Role Checking
```java
// Use Spring Security expressions in @PreAuthorize
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
```
##### 2. Custom Authorization Logic
```java
// For complex rules, use bean references
@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
```
##### 3. Manual Role Checking in Service Methods
```java
if (hasAdminRole(authentication)) {
    // ADMIN logic
} else if (hasEditorRole(authentication)) {
    // EDITOR logic
} else {
    throw new AccessDeniedException("Insufficient permissions");
}
```
#### Role Constants
Always use constants from RoleConstants class:

```java
import com.example.phoebe.security.RoleConstants;

// CORRECT

hasAuthority(authentication, RoleConstants.ROLE_ADMIN)

        // INCorrect - hardcoded strings
        hasAuthority(authentication, "ROLE_ADMIN")
```
#### Summary
The corrected implementation uses:

Spring Security's built-in role checking (hasRole())

Custom authorization methods for complex rules (@bean.method())

Repository-level author verification (existsByIdAndAuthorId)

Consistent role constants via RoleConstants class

Comprehensive testing of all authorization scenarios

This approach eliminates the previous contradictions and provides a clean, maintainable security implementation.