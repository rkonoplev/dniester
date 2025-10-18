# Руководство по реализации безопасности на основе ролей

> Для определения ключевых терминов и технологий, пожалуйста, обратитесь к **[Глоссарию](./GLOSSARY_RU.md)**.

Этот документ описывает **правильные** требования к реализации безопасности для ролей ADMIN и EDITOR в Phoebe CMS.

## Определения ролей

### Роль ADMIN
- **Полный доступ к системе** - может выполнять любые операции.
- **Управление всем контентом** - создание, чтение, обновление, удаление любых статей.
- **Управление пользователями** - управление профилями и ролями пользователей.
- **Конфигурация системы** - управление терминами, категориями, настройками.

### Роль EDITOR
- **Только собственный контент** - может управлять только статьями, автором которых он является.
- **Создание контента** - может создавать новые статьи (становится их автором).
- **Операции, ограниченные авторством** - редактирование/удаление только там, где `author_id` совпадает с ID
  пользователя.
- **Контроль публикации** - публикация/снятие с публикации только собственных статей.
- **Доступ только для чтения** - может просматривать, но не изменять чужой контент.

## Реализация безопасности

### 1. Безопасность на уровне сервисов (ПРАВИЛЬНАЯ РЕАЛИЗАЦИЯ)

#### Методы NewsService - Правильная авторизация

```java
// ADMIN: полный доступ, EDITOR: только свой контент
@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication auth) {
    // Проверка безопасности выполняется через @PreAuthorize
}

@PreAuthorize("@newsServiceImpl.canAccessNews(#id, authentication)")
public void delete(Long id, Authentication auth) {
    // Проверка безопасности выполняется через @PreAuthorize
}

// ADMIN: весь контент, EDITOR: только свой контент
@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
public Page<NewsDto> findAll(Pageable pageable, Authentication auth) {
    // Ручная фильтрация внутри метода в зависимости от роли
    if (hasAdminRole(auth)) {
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    } else {
        Long authorId = getCurrentUserId(auth);
        return newsRepository.findByAuthorId(authorId, pageable).map(newsMapper::toDto);
    }
}
```

### 2. Методы сервиса авторизации

```java
public boolean canAccessNews(Long newsId, Authentication authentication) {
    if (hasAdminRole(authentication)) {
        return true; // ADMIN может получить доступ к любой новости
    }
    
    if (hasEditorRole(authentication)) {
        // EDITOR может получить доступ только к своим новостям
        return isAuthor(newsId, authentication);
    }
    
    return false;
}

public boolean isAuthor(Long newsId, Authentication authentication) {
    Long currentUserId = getCurrentUserId(authentication);
    return newsRepository.existsByIdAndAuthorId(newsId, currentUserId);
}
```

### 3. Улучшения на уровне репозитория

```java
// Проверка, является ли пользователь автором конкретной статьи
boolean existsByIdAndAuthorId(Long id, Long authorId);

// Для роли EDITOR - находить только собственный контент
Page<News> findByAuthorId(Long authorId, Pageable pageable);
```

### 4. Конфигурация безопасности

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ← ВКЛЮЧАЕТ @PreAuthorize
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

### 5. Стратегия тестирования

```java
@Test
void findAll_AdminRole_ShouldReturnAllNews() {
    // Учитывая аутентификацию администратора
    Authentication adminAuth = createAdminAuthentication();
    // Когда
    var result = newsService.findAll(pageable, adminAuth);
    // Тогда - должны вернуться все новости
    assertEquals(3, result.getContent().size());
}

@Test
void update_EditorRole_ShouldDenyUpdatingOthersNews() {
    // Редактор пытается обновить чужую новость - должно завершиться ошибкой
    assertThrows(AccessDeniedException.class, 
        () -> newsService.update(1L, request, editorAuth));
}
```
