# Complete Project Information

## Project Overview
**Name**: News Platform  
**Type**: Modern news publishing platform (monorepo)  
**Migration**: Drupal 6 → Spring Boot + React  
**Status**: Backend complete, Frontend planned  

## Technology Stack

### Backend (Production Ready)
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: MySQL 8.0 (H2 for tests)
- **Security**: Spring Security with Basic Auth (Google OAuth2 planned)
- **API**: REST with OpenAPI/Swagger documentation
- **Build**: Gradle 8.7
- **Testing**: JUnit 5, Integration tests with H2
- **Migration**: Flyway (disabled in favor of Hibernate DDL)

### Frontend (Planned)
- Next.js (React) with Material UI for Google News–inspired design  
- Responsive, mobile-first layout with clean grid system  
- Static SEO-friendly URLs for all articles (SSR/SSG enabled)  
- Structured data (JSON-LD) + OpenGraph metadata for search engines  
- Branding with custom color palette (dark blue, red, white) and typography  
- Planned enhancements: search, dark mode, push notifications

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Code Quality**: Checkstyle, JaCoCo coverage
- **Database**: MySQL with Docker
- **Rate Limiting**: Bucket4j with IP-based buckets

## Project Structure
```
news-platform/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/example/newsplatform/
│   │   ├── config/            # Security, Test, Rate Limit configurations
│   │   ├── controller/        # REST endpoints (Admin + Public)
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA entities (News, User, Term, Role)
│   │   ├── exception/        # Custom exceptions + Global handler
│   │   ├── filter/           # Rate limiting filter
│   │   ├── mapper/           # Entity-DTO mapping
│   │   ├── repository/       # JPA repositories
│   │   └── service/          # Business logic
│   ├── src/main/resources/
│   │   ├── db/migration/     # Flyway SQL scripts
│   │   ├── application*.yml  # Environment configurations
│   │   └── static/           # Static resources
│   └── src/test/             # Unit + Integration tests
├── frontend/                  # Future Gatsby application
├── docs/                     # Documentation
├── .github/workflows/        # CI/CD pipelines
├── docker-compose.yml        # Development environment
└── README.md                 # Project documentation
```

## Core Entities & Database Schema

### News (content table)
- **Fields**: id, title, body, teaser, publicationDate, published, createdAt, updatedAt
- **Relationships**: ManyToOne → User (author), ManyToMany → Term (categories/tags)
- **Indexes**: title, published, publication_date

### User (users table)
- **Fields**: id, username, email, active
- **Relationships**: OneToMany → News, ManyToMany → Role
- **Security**: Basic Auth with BCrypt passwords

### Term (terms table)
- **Fields**: id, name, vocabulary (category/tag grouping)
- **Relationships**: ManyToMany → News
- **Constraints**: Unique(name, vocabulary)

### Role (roles table)
- **Fields**: id, name
- **Relationships**: ManyToMany → User

## API Endpoints

### Public API (`/api/public/news`) - Rate Limited: 100 req/min per IP
- `GET /` - Search published news (pagination, filters)
- `GET /{id}` - Get published article by ID
- `GET /term/{termId}` - Get news by specific term ID (pagination)
- `GET /terms?termIds=1,3,5` - Get news by multiple term IDs (pagination)

### Admin API (`/api/admin/news`) - Requires ADMIN role, Rate Limited: 50 req/min per IP
- `GET /` - Search all news (published + unpublished)
- `POST /` - Create new article
- `PUT /{id}` - Update existing article
- `DELETE /{id}` - Delete article

### Pagination Support
- **Parameters**: `page=0&size=10&sort=publicationDate,desc`
- **Configurable page sizes**: 10, 15, 20+ articles per page
- **Sorting**: By publication date (latest first) or any field
- **Response format**: JSON with `content`, `totalElements`, `totalPages`, `size`

## Configuration Profiles

### application.yml (default)
- Base configuration
- Environment variable placeholders

### application-prod.yml
- Production MySQL configuration
- Environment-based credentials
- Optimized JPA settings

### application-ci.yml
- H2 in-memory database
- Fast test execution
- Minimal logging

### application-test.yml
- H2 with MySQL compatibility mode
- Test security configuration
- Bean override support

## Security Configuration

### Main Security (SecurityConfig)
- **Authentication**: HTTP Basic Auth
- **Authorization**: Role-based (`/api/admin/**` requires ADMIN)
- **CSRF**: Disabled (REST API)
- **User Management**: In-memory with configurable admin credentials

### Test Security (TestSecurityConfig)
- **Override**: Uses `@Primary` to replace main config in tests
- **Permissions**: All requests permitted
- **Credentials**: Test admin user

## Build & CI/CD

### Gradle Tasks
- `./gradlew build` - Full build with tests
- `./gradlew test` - Run tests
- `./gradlew jacocoTestReport` - Generate coverage
- `./gradlew checkstyleMain` - Code style validation

### GitHub Actions (gradle-ci.yml)
- **Triggers**: Push to main, Pull Requests
- **Java**: OpenJDK 21
- **Steps**: Build → Test → Coverage → Static Analysis
- **Artifacts**: Test reports, coverage data

### Code Quality
- **Checkstyle**: 120 character line limit, Java conventions
- **JaCoCo**: Test coverage reporting
- **CodeCov**: Coverage tracking integration

## Docker Configuration

### Development (docker-compose.yml)
- **MySQL 8.0**: Port 3306, persistent volume
- **Spring Boot**: Port 8080, auto-restart
- **Environment**: Uses .env.dev file

### Database
- **Schema**: Auto-created by Hibernate (ddl-auto: update)
- **Data**: Sample data via Flyway migrations
- **Charset**: utf8mb4 for full Unicode support

## Migration from Drupal 6

### Completed
- **Schema mapping**: Drupal nodes → News entities
- **User migration**: Drupal users → Spring Security users
- **Taxonomy**: Drupal terms → Term entities
- **Content**: Articles with categories and authors

### Entity ID Strategy
- **Auto-generation**: `@GeneratedValue(IDENTITY)` for new records
- **Migration compatibility**: Can handle explicit IDs when needed

## Testing Strategy

### Unit Tests
- **Mappers**: Entity ↔ DTO conversion
- **Services**: Business logic validation
- **Controllers**: Endpoint behavior

### Integration Tests
- **Database**: H2 in-memory with MySQL mode
- **Security**: Isolated test configuration
- **Transactions**: `@Transactional` for cleanup

### Test Configuration
- **Profiles**: Separate test profile with H2
- **Security**: Overridden with permissive config
- **Data**: Auto-generated test entities

## Current Status & Next Steps

### Completed ✅
- Core backend functionality
- REST API with full CRUD operations
- Security implementation
- Database schema and relationships
- Docker development environment
- CI/CD pipeline with quality gates
- Integration tests
- API documentation (Swagger)
- **Term-based pagination**: Filter news by taxonomy terms (categories/tags)
- **Flexible pagination**: Configurable page sizes and sorting options
- **Multiple term filtering**: Checkbox-based term selection support
- **Rate limiting**: IP-based request throttling with Bucket4j (100/min public, 50/min admin)

### In Progress 🔄
- Code quality improvements
- Security vulnerability fixes
- Performance optimizations

### Planned 📋
- Frontend development (Next.js)
- Production deployment
- Advanced search features
- File upload capabilities
- Email notifications

## Known Issues & Technical Debt

### Security
- CORS configuration needs refinement for production
- **Google OAuth2 migration planned**: Replace Basic Auth with Google Sign-In for ADMIN, EDITOR, USER roles
- Input validation and sanitization improvements

### Performance
- Database query optimization needed
- Consider caching for frequently accessed data
- Pagination improvements for large datasets

### Code Quality
- Reduce code duplication in mappers
- Improve error handling consistency
- Add more comprehensive validation

## Environment Variables

### Required for Production
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newsdb
SPRING_DATASOURCE_USERNAME=newsuser
SPRING_DATASOURCE_PASSWORD=secure_password

# Admin Credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=secure_admin_password

# Server
SPRING_SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### Development (.env.dev)
```bash
# Example development environment variables
# Actual values are in .env.dev (excluded from git)
MYSQL_ROOT_PASSWORD=<dev_password>
MYSQL_DATABASE=newsdb
MYSQL_USER=<dev_user>
MYSQL_PASSWORD=<dev_password>
ADMIN_USERNAME=<dev_admin>
ADMIN_PASSWORD=<dev_admin_password>
```

## Key Design Decisions

1. **Monorepo Structure**: Backend and frontend in single repository
2. **JPA over MyBatis**: Hibernate for ORM with automatic schema generation
3. **Basic Auth**: Simple authentication suitable for admin interface
4. **H2 for Tests**: Fast, isolated test execution
5. **Docker First**: Development environment containerized
6. **REST over GraphQL**: Traditional REST API with OpenAPI documentation
7. **Gradle over Maven**: Build tool choice for dependency management

This document provides complete context for understanding the project architecture, current state, and technical decisions made during development.