# Complete Project Information

## Project Overview
**Name**: Phoebe — Headless CMS for News Agencies & Digital Media  
**Type**: Open Source Headless Content Management System (Hybrid Architecture)
**Migration**: Drupal 6 → Modern Headless Spring Boot + Optional Angular Frontend
**Status**: Headless Backend production-ready, Reference Frontend planned
**License**: MIT (Open Source)  

## Technology Stack

### Backend (Production Ready)
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: MySQL 8.0 (H2 for tests)
- **Security**: Spring Security with Basic Auth (OAuth 2.0 + 2FA planned for ADMIN and EDITOR roles)
- **API**: REST with OpenAPI/Swagger documentation
- **Caching**: Caffeine (In-Memory)
- **Build**: Gradle 8.7
- **Testing**: JUnit 5, Integration tests with H2
- **Migration**: Flyway (disabled in favor of Hibernate DDL)

### Reference Frontend (Optional)
- **Framework**: Angular with Angular Universal
- **Purpose**: Reference implementation for small media organizations
- **Design**: Google News–inspired responsive layout
- **SEO**: Static URLs, SSR, JSON-LD, OpenGraph metadata
- **Branding**: Customizable theme system
- **Features**: Search, dark mode, push notifications (planned)
- **Note**: Professional teams can build custom frontends using the headless API

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Code Quality**: Checkstyle, JaCoCo coverage
- **Database**: MySQL with Docker
- **Rate Limiting**: Bucket4j with IP-based buckets

## Hybrid Headless Architecture

**Phoebe** follows a **Hybrid Headless** approach, providing maximum flexibility:

- **Headless Core**: Complete REST API for custom frontend development
- **Reference Frontend**: Optional Angular application for quick deployment
- **API-First**: All functionality accessible via REST endpoints
- **No Vendor Lock-in**: Use our frontend or build your own

## Project Structure
```
phoebe/
├── backend/                    # Headless Spring Boot API
│   ├── src/main/java/com/example/newsplatform/
│   │   ├── config/            # Security, Test, Rate Limit, Cache configurations
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
├── frontend/                  # Reference Angular UI (optional)
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

## Headless API Endpoints

### Public Content API (`/api/public/news`) - Rate Limited: 100 req/min per IP
- `GET /` - Search published news (pagination, filters)
- `GET /{id}` - Get published article by ID
- `GET /term/{termId}` - Get news by specific term ID (pagination)
- `GET /terms?termIds=1,3,5` - Get news by multiple term IDs (pagination)

### Content Management API (`/api/admin/news`) - Requires ADMIN role, Rate Limited: 50 req/min per IP
- `GET /` - Search all news (published + unpublished)
- `POST /` - Create new article
- `PUT /{id}` - Update existing article
- `DELETE /{id}` - Delete article

### Use Cases for Headless API
- **Custom Frontends**: Build with React, Vue, Angular, or any framework
- **Mobile Applications**: Native iOS/Android apps
- **Third-Party Integrations**: Telegram bots, email newsletters
- **Multi-Platform Publishing**: Websites, AMP pages, social media
- **Analytics & Advertising**: Connect with external services

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

## Code Quality
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

## Current Status & Roadmap

### ✅ Completed (Headless Core)
- **Headless API**: Complete REST API with OpenAPI/Swagger documentation
- **Backend Core**: Spring Boot 3.x with Java 21
- **Database**: MySQL 8.0 with H2 for tests
- **Security**: Spring Security with configurable authentication
- **Content Management**: Full CRUD operations via API
- **Pagination**: Advanced filtering and pagination support
- **Caching**: High-performance in-memory caching with Caffeine
- **Rate Limiting**: IP-based API protection with Bucket4j
- **CI/CD**: GitHub Actions with automated testing
- **Docker**: Development environment with Docker Compose
- **Migration**: Drupal 6 → Headless CMS migration tools
- **Open Source**: MIT license for community use
- **Code Quality**: Eliminated code duplication in mappers with BaseMapper
- **Error Handling**: Consistent exception system with standardized error codes

### 🚧 In Progress
- **Documentation**: Headless API integration guides
- **Reference Frontend**: Angular implementation
- **Performance**: API optimization for high-traffic scenarios

### 🎯 Planned
- **Authentication**: OAuth 2.0 + 2FA for enhanced security
- **Advanced Features**: File uploads, advanced search, webhooks
- **Integrations**: Third-party service connectors
- **Deployment**: Cloud-native deployment guides
- **Community**: Plugin system for extensions

## Known Issues & Technical Debt

### ✅ Resolved
- ~~Code duplication in mappers~~ → Implemented BaseMapper
- ~~Missing permissions system~~ → Added permissions system with V5/V6 migrations
- ~~Inconsistent error handling~~ → Global exception handler implemented
- ~~Missing database migration docs~~ → DATABASE_MIGRATION_GUIDE.md created
- ~~CMS vs MySQL password confusion~~ → Documentation clarified

### 🔄 In Progress
- **Reference Angular Frontend**: Basic structure created, requires component implementation
- **API Performance**: Query optimization for high-traffic scenarios

### 🎯 Future Technical Debt

#### Security (High Priority)
- **OAuth 2.0 + JWT**: Replace Basic Auth with modern authentication
- **2FA for ADMIN/EDITOR**: Two-factor authentication for critical roles
- **Production CORS**: Configuration for multi-domain frontends
- **User-based Rate Limiting**: Supplement IP-based restrictions

#### Functionality (Medium Priority)
- **File Upload**: Support for images and media in articles
- **Advanced Search**: Full-text search with Elasticsearch/Lucene
- **Webhooks**: Event notifications for integrations
- **Content Versioning**: Article change history
- **Publication Scheduler**: Delayed article publishing

#### Performance (Medium Priority)
- **Distributed Caching**: Redis for multi-instance deployments
- **Database Optimization**: Indexes for complex queries
- **Cursor Pagination**: For very large datasets
- **CDN Integration**: For static resources

#### DevOps (Low Priority)
- **Kubernetes Manifests**: For cloud-native deployments
- **Monitoring**: Prometheus + Grafana metrics
- **Structured Logging**: ELK stack integration
- **Backup Automation**: Regular database backups

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

1. **Hybrid Headless Architecture**: API-first with optional reference frontend
2. **Open Source**: MIT license for community adoption and contribution
3. **Media-Focused**: Designed specifically for news agencies and digital media
4. **API-First**: All functionality accessible via REST endpoints
5. **No Vendor Lock-in**: Freedom to choose any frontend technology
6. **Professional Grade**: Enterprise-ready security, caching, and rate limiting
7. **Migration-Friendly**: Tools for migrating from legacy CMS platforms
8. **Docker First**: Containerized development and deployment
9. **Modern Stack**: Java 21, Spring Boot 3.x, MySQL 8.0
10. **Community-Driven**: Extensible architecture for plugins and integrations

## Target Audience

### Professional Editorial Teams
- News agencies requiring custom frontend solutions
- Digital media companies with existing mobile apps
- Organizations needing multi-platform content distribution
- Teams requiring integration with analytics and advertising platforms

### Small Media Organizations
- Local newspapers needing quick deployment
- Startups requiring cost-effective CMS solutions
- Organizations migrating from legacy platforms like Drupal
- Teams wanting to start with a reference implementation and customize later

This document provides complete context for understanding Phoebe's headless architecture, current
capabilities, and vision for modern media content management.