# Input Validation and Content Processing Guide

## Overview

Phoebe CMS implements comprehensive input validation and content processing to ensure data integrity,
security, and user experience. This guide covers all validation mechanisms, HTML sanitization, and
YouTube content processing.

## Validation Architecture

### Core Components

1. **BaseException System** - Standardized error handling with HTTP status codes
2. **SafeHtml Validation** - Custom HTML sanitization and YouTube processing
3. **Entity Validation** - Bean Validation (Jakarta Validation / JSR-380) annotations
4. **Content Processing Service** - Automated content transformation

### Validation Placement in the Application

Input validation in the Phoebe CMS Spring Boot application is typically applied at the following layers:

-   **Controller Layer**: This is the first place where incoming DTOs (Data Transfer Objects) or entities are validated. `@Valid` or `@Validated` annotations are used along with Bean Validation annotations. This ensures that invalid data does not reach the business logic.
-   **Service Layer**: In some cases, when validation depends on complex business logic or database state, it may be performed at the service layer. This is also useful for validating nested objects or collections, where `@Valid` or `@Validated` can be applied to DTO fields containing other DTOs or lists.

## SafeHtml Validation

### Purpose
Protects against XSS attacks while allowing safe HTML content and automatic YouTube embed conversion.

### Allowed HTML Tags
- `img` - Images
- `b`, `strong` - Bold text
- `i`, `em` - Italic/emphasized text
- `a` - Links
- `u` - Underlined text
- `p` - Paragraphs
- `iframe` - YouTube embeds (auto-generated)
- `div` - Container for responsive YouTube embeds

### YouTube Link Processing

#### Supported Formats
- Standard: `https://www.youtube.com/watch?v=VIDEO_ID`
- Short: `https://youtu.be/VIDEO_ID`

#### Automatic Conversion
YouTube links are automatically converted to responsive embed code:

```html
<div style="position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden;">
  <iframe src="https://www.youtube.com/embed/VIDEO_ID" 
          style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;" 
          frameborder="0" allowfullscreen></iframe>
</div>
```

#### Responsive Design
- 16:9 aspect ratio maintained
- Adapts to device screen size
- Mobile-friendly implementation

## Entity Validation Rules

### Validation of Nested Objects and Collections

For validating fields that are themselves objects (DTOs) or collections of objects, `@Valid` or `@Validated` annotations are used. For example:

```java
public class ParentDTO {
    @NotBlank
    private String name;

    @Valid // Validates the nested ChildDTO object
    private ChildDTO child;

    @Valid // Validates each element in the collection
    private List<AnotherChildDTO> childrenList;

    // ... getters and setters
}
```

### News Entity

| Field | Validation Rules | Description |
|-------|------------------|-------------|
| `title` | `@NotBlank`, `@Size(max=50)` | Required, max 50 characters |
| `body` | `@SafeHtml` | Allows safe HTML + YouTube embeds |
| `teaser` | `@SafeHtml`, `@Size(max=250)` | Safe HTML, max 250 characters |
| `author` | `@NotNull` | Required author reference |

### User Entity

| Field | Validation Rules | Description |
|-------|------------------|-------------|
| `username` | `@NotBlank`, `@Size(min=3, max=50)` | 3-50 characters, unique |
| `email` | `@NotBlank`, `@Email`, `@Size(max=100)` | Valid email format |
| `password` | `@NotBlank`, `@Size(min=8)` | Minimum 8 characters |
| `firstName` | `@Size(max=50)` | Optional, max 50 characters |
| `lastName` | `@Size(max=50)` | Optional, max 50 characters |

### Term Entity

| Field | Validation Rules | Description |
|-------|------------------|-------------|
| `name` | `@NotBlank`, `@Size(max=100)` | Required, max 100 characters |
| `description` | `@Size(max=500)` | Optional, max 500 characters |

### Role Entity

| Field | Validation Rules | Description |
|-------|------------------|-------------|
| `name` | `@NotBlank`, `@Size(max=50)` | Required, max 50 characters |
| `description` | `@Size(max=200)` | Optional, max 200 characters |

## Exception Handling

### Exception Hierarchy
- `BaseException` - Abstract base with error codes
- `ValidationException` - HTTP 400 (Bad Request)
- `BusinessException` - HTTP 409 (Conflict)

### Error Codes
- `VALIDATION_ERROR` - Input validation failed
- `BUSINESS_RULE_VIOLATION` - Business logic constraint violated
- `UNSAFE_HTML_CONTENT` - HTML sanitization failed

### Example of Standardized Validation Error Response

When a `ValidationException` or other validation errors occur, the API returns a standardized JSON response that includes the HTTP status, error code, message, and, if necessary, field-specific details:

```json
{
  "timestamp": "2023-10-27T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Input validation error",
  "details": [
    {
      "field": "title",
      "message": "must not be blank"
    },
    {
      "field": "email",
      "message": "must be a valid email address"
    }
  ],
  "path": "/api/admin/news"
}
```

## Content Processing Service

### Relationship between SafeHtml and ContentProcessingService

The `@SafeHtml` annotation is used to mark fields that require HTML sanitization and YouTube link processing. `SafeHtmlValidator` (the validator implementation for `@SafeHtml`) uses `ContentProcessingService` to perform the actual sanitization and transformation. Thus, `ContentProcessingService` is the central component that executes all complex content processing logic, and `@SafeHtml` provides a convenient declarative way to apply this logic.

### Usage
```java
@Autowired
private ContentProcessingService contentService;

public void processNewsContent(News news) {
    String processedBody = contentService.processContent(news.getBody());
    news.setBody(processedBody);
}
```

### Features
- Automatic YouTube link detection and conversion
- HTML sanitization validation
- Responsive embed code generation
- XSS protection

## Security Considerations

### XSS Prevention
- All HTML content validated against whitelist
- JavaScript execution blocked
- Only safe HTML tags allowed
- YouTube embeds use the trusted domain `youtube.com` to ensure security, which is hardcoded in the conversion logic.

### Input Sanitization
- Bean Validation annotations on all entities
- Custom SafeHtml validator for content fields
- Automatic content processing before storage

## Testing

### Validation Tests
```bash
./gradlew test --tests SafeHtmlValidatorTest
./gradlew test --tests ContentProcessingServiceTest
```

### Test Coverage
- YouTube link conversion (standard and short URLs)
- HTML tag validation
- Responsive embed generation
- XSS attack prevention

## Implementation Files

### Core Classes
- `SafeHtml.java` - Custom validation annotation
- `SafeHtmlValidator.java` - HTML sanitization logic
- `ContentProcessingService.java` - Content transformation
- `BaseException.java` - Exception hierarchy base
- `GlobalExceptionHandler.java` - Centralized exception handling and standardized response generation.

### Entity Classes
- `News.java` - News article validation
- `Permission.java` - Permission validation
- `User.java` - User account validation
- `Term.java` - Taxonomy term validation
- `Role.java` - User role validation

## Best Practices

1. **Always validate user input** at the entity (DTO) and/or service layer.
2. **Use SafeHtml annotation** for content fields containing HTML.
3. **Process content** before storage using ContentProcessingService.
4. **Handle exceptions** with proper HTTP status codes and standardized responses.
5. **Test validation rules** and content processing logic thoroughly.
6. **Keep allowed HTML tags minimal** for maximum security.
7. **Use `@Valid` or `@Validated`** for validating nested objects and collections.