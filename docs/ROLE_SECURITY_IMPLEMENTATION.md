# 🔐 Role-Based Security Implementation Guide

This document outlines the implementation requirements for ADMIN and EDITOR role security in the News Platform.

## 📋 Role Definitions

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

## 🛡️ Security Implementation Requirements

### 1. Service Layer Security

#### NewsService Methods - Required Authorization Checks

```java
// ADMIN: Full access, EDITOR: Own content only
public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication auth) {
    // Check if user is ADMIN or (EDITOR and author of the article)
}

public void delete(Long id, Authentication auth) {
    // Check if user is ADMIN or (EDITOR and author of the article)  
}

// ADMIN: All content, EDITOR: Own content only
public Page<NewsDto> searchAll(String search, String category, Pageable pageable, Authentication auth) {
    // ADMIN: return all results
    // EDITOR: filter by author_id = current user ID
}
```

#### Required Security Annotations
```java
@PreAuthorize("hasRole('ADMIN') or (hasRole('EDITOR') and @newsService.isAuthor(#id, authentication.name))")
public NewsDto update(Long id, NewsUpdateRequestDto request);

@PreAuthorize("hasRole('ADMIN') or (hasRole('EDITOR') and @newsService.isAuthor(#id, authentication.name))")  
public void delete(Long id);
```

### 2. Controller Layer Updates

#### AdminNewsController - Required Changes

```java
// Add Authentication parameter to methods
public ResponseEntity<NewsDto> update(@PathVariable Long id, 
                                    @RequestBody @Valid NewsDto newsDto,
                                    Authentication authentication) {
    // Pass authentication to service layer
}

public ResponseEntity<Void> delete(@PathVariable Long id, 
                                 Authentication authentication) {
    // Pass authentication to service layer  
}
```

### 3. Repository Layer Enhancements

#### NewsRepository - Additional Query Methods

```java
// For EDITOR role - find only own content
Page<News> findByAuthorId(Long authorId, Pageable pageable);

// Check if user is author of specific article
boolean existsByIdAndAuthorId(Long id, Long authorId);

// Search with author filter for EDITOR role
@Query("SELECT n FROM News n WHERE (:authorId IS NULL OR n.author.id = :authorId) AND ...")
Page<News> searchAllByAuthor(String search, String category, Long authorId, Pageable pageable);
```

### 4. Security Configuration Updates

#### SecurityConfig - Role-Based Access

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
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

### 5. Service Implementation Example

#### NewsServiceImpl - Author Verification

```java
@Service
@Transactional
public class NewsServiceImpl implements NewsService {
    
    public boolean isAuthor(Long newsId, String username) {
        return newsRepository.existsByIdAndAuthorUsername(newsId, username);
    }
    
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EDITOR') and @newsService.isAuthor(#id, authentication.name))")
    public NewsDto update(Long id, NewsUpdateRequestDto request) {
        // Implementation with security check
        News existing = newsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("News not found with id " + id));
            
        // Update logic...
        return NewsMapper.toDto(updated);
    }
    
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable, Authentication auth) {
        if (hasRole(auth, "ADMIN")) {
            return newsRepository.searchAll(search, category, pageable).map(NewsMapper::toDto);
        } else if (hasRole(auth, "EDITOR")) {
            Long authorId = getCurrentUserId(auth);
            return newsRepository.searchAllByAuthor(search, category, authorId, pageable).map(NewsMapper::toDto);
        }
        throw new AccessDeniedException("Insufficient permissions");
    }
}
```

## 🧪 Testing Requirements

### Security Test Cases

1. **ADMIN Role Tests**
   - Can access all endpoints
   - Can modify any content
   - Can perform bulk operations

2. **EDITOR Role Tests**  
   - Can create new content
   - Can only edit own content
   - Cannot edit others' content (403 Forbidden)
   - Cannot access user management endpoints
   - Cannot perform system-wide bulk operations

3. **Authorization Tests**
   - Verify `@PreAuthorize` annotations work correctly
   - Test author verification logic
   - Test role-based filtering in search results

## 📝 Implementation Checklist

- [ ] Add `Authentication` parameters to controller methods
- [ ] Implement `@PreAuthorize` annotations on service methods  
- [ ] Add author verification methods to service layer
- [ ] Create role-based repository query methods
- [ ] Update security configuration for method-level security
- [ ] Add comprehensive security tests
- [ ] Update API documentation with role restrictions
- [ ] Implement proper error handling for authorization failures

## 🔗 Related Files to Update

- `AdminNewsController.java` - Add authentication parameters
- `NewsServiceImpl.java` - Add authorization checks  
- `NewsRepository.java` - Add author-based queries
- `SecurityConfig.java` - Enable method security
- Integration tests - Add role-based test cases
- API documentation - Update with role restrictions

This implementation ensures that EDITOR users can only manage content they have authored, while ADMIN users maintain full system access.