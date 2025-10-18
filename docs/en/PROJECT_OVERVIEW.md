# Phoebe CMS — Project Overview

## Project Summary

**Name**: Phoebe — Headless CMS for News Agencies & Digital Media  
**Type**: Open Source Headless Content Management System (Hybrid Architecture)  
**Migration**: Drupal 6 → Modern Headless Spring Boot + Optional Angular Frontend  
**Status**: Headless Backend production-ready, Reference Frontend planned  
**License**: MIT (Open Source)

## Business Goals

1. Replace outdated Drupal 6 backend with modern headless architecture
2. Improve editorial workflow with intuitive content management
3. Optimize performance for high-traffic news scenarios
4. Enable future integrations: push notifications, analytics, mobile apps
5. Provide API-first approach for custom frontend development

## Hybrid Headless Architecture

**Phoebe** follows a **Hybrid Headless** approach, providing maximum flexibility:

- **Headless Core**: Complete REST API for custom frontend development
- **Reference Frontend**: Optional Angular application for quick deployment
- **API-First**: All functionality accessible via REST endpoints
- **No Vendor Lock-in**: Use our frontend or build your own

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
│   └── src/test/             # Unit + Integration tests
├── frontend/                  # Reference Angular UI (optional)
├── docs/                     # Documentation
└── README.md                 # Project documentation
```

## Technology Stack

### Backend (Production Ready)
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: MySQL 8.0, PostgreSQL 12+ (H2 for tests)
- **Security**: Spring Security with Basic Auth (OAuth 2.0 + 2FA planned)
- **API**: REST with OpenAPI/Swagger documentation
- **Caching**: Caffeine (In-Memory)
- **Build**: Gradle 8.7
- **Testing**: JUnit 5, Integration tests with H2
- **Rate Limiting**: Bucket4j with IP-based buckets

### Reference Frontend (Optional)
- **Framework**: Angular with Angular Universal
- **Purpose**: Reference implementation for small media organizations
- **Design**: Google News–inspired responsive layout
- **SEO**: Static URLs, SSR, JSON-LD, OpenGraph metadata
- **Features**: Search, dark mode, push notifications (planned)

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Code Quality**: Checkstyle, JaCoCo coverage
- **Database**: MySQL or PostgreSQL with Docker

## Configuration Profiles

The project uses Spring Boot profiles to manage configuration across different environments (local, test, production).
Each profile is represented by a separate `application-{profile}.yml` file.

A detailed description of all profiles and their settings is available in the [Configuration Guide](CONFIG_GUIDE.md).

## Core Entities & API Overview

### Database Entities
- **News** (content table): Articles with title, body, teaser, publication workflow
- **User** (users table): Authentication with role-based access control
- **Term** (terms table): Taxonomy system with vocabulary grouping
- **Role** (roles table): ADMIN, EDITOR roles with granular permissions

### API Endpoints

#### Public Content API (`/api/public/news`) - Rate Limited: 100 req/min per IP
- `GET /` - Search published news (pagination, filters)
- `GET /{id}` - Get published article by ID
- `GET /term/{termId}` - Get news by specific term ID
- `GET /terms?termIds=1,3,5` - Get news by multiple term IDs

#### Content Management API (`/api/admin/news`) - Requires ADMIN role, Rate Limited: 50 req/min per IP
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

## Migration from Drupal 6

### Completed Migration Features
- **Schema mapping**: Drupal nodes → News entities
- **User migration**: Drupal users → Spring Security users
- **Taxonomy**: Drupal terms → Term entities with vocabulary preservation
- **Content**: Articles with categories and authors

### Vocabulary System & Archive Data Handling

**Drupal 6 Original Structure:**
- `vocabulary` table - vocabulary definitions (categories, tags, etc.)
- `term_data` table - terms linked to vocabularies via `vid` (vocabulary ID)
- `term_node` table - term-to-node relationships

**What Happens to Archive Terms:**

1. **All Drupal 6 terms HAD vocabularies** - they migrate correctly with original vocabulary names
2. **Terms without vocabulary get `vocabulary = NULL`** (rare case)
3. **Term-to-news relationships preserved** through `content_terms` table
4. **Archive news articles retain all original taxonomy classifications**

**Two Database Scenarios:**

**Scenario A: Migrated from Drupal 6**
- ✅ All archive terms have vocabulary from original Drupal 6 structure
- ✅ Vocabulary names like "category", "tags", "topics" preserved
- ✅ All news-term relationships maintained
- ✅ New content can use existing terms or create new ones

**Scenario B: Clean Database (Fresh Installation)**
- ✅ Spring Boot migrations (V1-V6) create basic structure
- ✅ Migration V3 creates sample term: "General" with vocabulary "category"
- ✅ New terms can be created with any vocabulary grouping
- ✅ System supports flexible taxonomy expansion

## Current Status & Roadmap

### ✅ Completed (Headless Core)
- **Headless API**: Complete REST API with OpenAPI/Swagger documentation
- **Backend Core**: Spring Boot 3.x with Java 21
- **Database Migration**: Moved from MariaDB → MySQL 8.0 for stable schema support
- **Dockerization**: Production-ready containers with one-command startup
- **Profile-based Configuration**: YAML configs for local/dev/test/ci/prod environments
- **Security**: Spring Security with role-based authorization (/api/public/ vs /api/admin/)
- **Global Exception Handling**: Unified JSON error responses with @ControllerAdvice
- **Input Validation**: DTO validation with annotations (@NotNull, @Size)
- **CORS Configuration**: Centralized security rules for multi-domain frontends
- **Content Management**: Full CRUD operations via API
- **Pagination**: Advanced filtering and pagination support
- **Caching**: High-performance in-memory caching with Caffeine
- **Rate Limiting**: IP-based API protection with Bucket4j
- **CI/CD**: GitHub Actions with automated testing
- **Docker**: Development environment with Docker Compose
- **Migration**: Drupal 6 → Headless CMS migration tools with UTF-8 encoding
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
- ~~Missing database migration docs~~ → DATABASE_GUIDE.md created
- ~~CMS vs MySQL password confusion~~ → Documentation clarified

### 🔄 In Progress
- **Reference Frontend**: Basic structure created, requires component implementation
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