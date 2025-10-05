# Phoebe — Headless CMS for News Agencies & Digital Media

[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)  
[![codecov](https://codecov.io/gh/rkonoplev/news-platform/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/rkonoplev/news-platform)  
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

> **Phoebe CMS** — Open source headless content management system engineered for news agencies and digital
> media organizations. Features hybrid architecture with powerful REST API, optional Angular frontend, and
> enterprise-grade security for modern journalism workflows.

**Open Source Headless CMS** designed specifically for news agencies, digital media, and professional
editorial teams.

## 🎯 Why Headless?

Headless architecture is the modern standard for professional media organizations that need:

- **Design Control**: Complete freedom over frontend design and user experience
- **Multi-Platform Publishing**: Mobile apps, Telegram bots, email newsletters, AMP pages
- **Third-Party Integrations**: Analytics, advertising, push notifications, social media
- **Scalability**: API-first approach for future growth
- **Flexibility**: Not locked into a single frontend solution

## 🏗️ Hybrid Architecture

Phoebe follows a **Hybrid Headless** approach:

```
phoebe/
├── backend/          ← Spring Boot API (headless core)
├── frontend/         ← Angular reference UI (optional)
└── docs/             ← Documentation: "use our frontend or build your own"
```

**Benefits:**
- **Professional Teams**: Use only the API → maximum flexibility
- **Small Media**: Deploy everything "out of the box" → simplicity
- **No Vendor Lock-in**: You're not tied to our frontend, but we provide a starting template

## 🚀 Quick Start

**Requirements:**
- JDK 21+
- Docker & Docker Compose
- Gradle (or use `./gradlew` wrapper)

### 1. Start backend dependencies (MySQL) via Docker Compose:
```bash
docker compose up -d
```

### 2. Start the Spring Boot application:
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=local'
```

**API entrypoint**: http://localhost:8080  
**Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 3. Optional: Start reference frontend (when implemented):
```bash
cd frontend
npm install
npm start
```

## 🔧 Key Features

- **Headless API**: Complete REST API for content management
- **Content Management**: Full CRUD operations for news articles
- **Taxonomy System**: Categories and tags with flexible filtering
- **User Management**: Role-based access (ADMIN, EDITOR, AUTHOR)
- **Security**: Spring Security with configurable authentication
- **Performance**: High-performance caching with Caffeine
- **Rate Limiting**: IP-based protection with Bucket4j
- **Database**: MySQL 8.0 with H2 for testing
- **CI/CD**: GitHub Actions pipeline with automated testing

## 📚 Documentation

### English Documentation
- **[Complete Project Information](docs/en/TASK_DESCRIPTION.md)** - Comprehensive project overview
- [Developer Guide](docs/en/DEVELOPER_GUIDE.md) - Development workflow and setup
- [API Usage Guide](docs/en/API_USAGE.md) - REST API examples and usage
- [Architecture Migration](docs/en/ARCHITECTURE_MIGRATION.md) - Technical architecture details
- [Configuration Guide](docs/en/CONFIG_GUIDE.md) - Environment and configuration setup
- [Authentication Guide](docs/en/AUTHENTICATION_GUIDE.md) - Security and authentication
- [Docker Guide](docs/en/DOCKER_GUIDE.md) - Docker deployment and development
- [Technical Specification](docs/en/TECHNICAL_SPEC.md) - Technical requirements and specifications
- [Frontend Specification](docs/en/FRONTEND_SPEC.md) - Reference frontend specifications
- [Database Schema](docs/en/DATABASE_SCHEMA.md) - Database structure and migrations
- [Input Validation Guide](docs/en/VALIDATION_GUIDE.md) - Comprehensive validation and content processing

### Russian Documentation (Русская документация)
- **[Полная информация о проекте](docs/ru/TASK_DESCRIPTION_RU.md)** - Подробный обзор проекта
- [Руководство разработчика](docs/ru/DEVELOPER_GUIDE_RU.md) - Процесс разработки и настройка
- [Быстрый старт](docs/ru/QUICK_START_RU.md) - Быстрый запуск для разработчиков
- [Настройка стиля кода](docs/ru/CODE_STYLE_SETUP_RU.md) - Настройка форматирования кода
- [Миграция с Drupal6](docs/ru/MIGRATION_DRUPAL6_RU.md) - Руководство по миграции
- [Руководство по валидации](docs/ru/VALIDATION_GUIDE_RU.md) - Валидация и обработка контента

## 🌟 Use Cases

### For Professional Editorial Teams
- Build custom frontends with your design system
- Integrate with existing mobile apps
- Create Telegram bots and email newsletters
- Connect analytics and advertising platforms
- Implement custom workflows and approval processes

### For Small Media Organizations
- Use the reference frontend for quick deployment
- Customize the provided Angular template
- Scale up to custom solutions as you grow

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Security
- **Database**: MySQL 8.0 (H2 for testing)
- **Caching**: Caffeine
- **Rate Limiting**: Bucket4j
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, TestContainers
- **CI/CD**: GitHub Actions
- **Reference Frontend**: Angular (planned)

## 🤝 Contributing

Contributions are welcome! This is an open source project designed to serve the media community.

- Check the [issues](../../issues) page for current tasks
- Open a [Pull Request](../../pulls) to propose changes
- Follow our [Code Style Guide](docs/en/CODE_STYLE_SETUP.md)

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.

See [DISCLAIMER.md](docs/en/DISCLAIMER.md) for legal information and warranty disclaimers.
See [NOTICE.md](docs/en/NOTICE.md) for third-party licenses and attributions.

---

**Phoebe CMS** - Empowering modern media with headless flexibility and professional-grade features.