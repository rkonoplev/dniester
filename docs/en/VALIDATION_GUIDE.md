# Input Validation and Content Processing Guide

## Overview

Phoebe CMS implements comprehensive input validation and content processing to ensure data integrity,
security, and user experience. This guide covers all validation mechanisms, HTML sanitization, and
YouTube content processing.

## Validation Architecture

### Core Components

1. **BaseException System** - Standardized error handling with HTTP status codes
2. **SafeHtml Validation** - Custom HTML sanitization and YouTube processing
3. **Entity Validation** - Bean Validation (JSR-303) annotations
4. **Content Processing Service** - Automated content transformation

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

## Content Processing Service

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
- YouTube embeds use trusted domain

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

### Entity Classes
- `News.java` - News article validation
- `Permission.java` - Permission validation
- `User.java` - User account validation
- `Term.java` - Taxonomy term validation
- `Role.java` - User role validation

## Best Practices

1. **Always validate user input** at entity level
2. **Use SafeHtml annotation** for content fields
3. **Process content** before storage using ContentProcessingService
4. **Handle exceptions** with proper HTTP status codes
5. **Test validation rules** thoroughly
6. **Keep allowed HTML tags minimal** for security