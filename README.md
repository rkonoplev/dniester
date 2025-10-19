# Phoebe — Open Source Headless CMS

[![Java CI with Gradle](https://github.com/rkonoplev/phoebe/actions/workflows/gradle-ci.yml/badge.svg)](
https://github.com/rkonoplev/phoebe/actions/workflows/gradle-ci.yml)  
[![codecov](https://codecov.io/gh/rkonoplev/phoebe/graph/badge.svg?token=YOUR_TOKEN)](
https://codecov.io/gh/rkonoplev/phoebe)  
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

> **Phoebe CMS** is a flexible, open-source headless content management system engineered for
> high-performance content delivery and modern development workflows.

**Phoebe CMS** is a modern, API-first content management system designed for developers and content
creators who need flexibility and performance.

## 🎯 Why Headless?

Headless architecture is the modern standard for professional organizations that need:

- **Design Control**: Complete freedom over frontend design and user experience.
- **Multi-Platform Publishing**: Deliver content to websites, mobile apps, and any other digital
  platform.
- **Third-Party Integrations**: Easily connect analytics, marketing, and other tools.
- **Scalability**: An API-first approach built for future growth.
- **Flexibility**: Avoid being locked into a single frontend solution.

## 🏗️ Hybrid Architecture

Phoebe follows a **Hybrid Headless** approach:

```
phoebe/
├── backend/          ← Spring Boot API (headless core)
├── frontend/         ← Angular reference UI (optional)
└── docs/             ← Documentation: "use our frontend or build your own"
```

**Benefits:**
- **Professional Teams**: Use only the API for maximum flexibility.
- **Startups & Small Teams**: Deploy everything "out of the box" for simplicity.
- **No Vendor Lock-in**: You're not tied to our frontend, but we provide a starting template.

## 🚀 Quick Start

To get started quickly with Phoebe CMS, follow these high-level steps. For detailed instructions,
refer to the dedicated guides.

**Requirements:**
- JDK 21+
- Docker & Docker Compose
- Git (for cloning the repository)

### 1. Initial Setup (First Time)
For cloning the repository, setting up environment variables, and running the project for the very
first time (including migration or clean installation), please see the comprehensive
**[Setup Guide](docs/en/SETUP_GUIDE.md)**.

### 2. Daily Development Workflow
For daily development tasks, including starting/stopping services, running the Spring Boot
application, executing tests, and troubleshooting, refer to the
**[Developer Guide](docs/en/DEVELOPER_GUIDE.md)**.

**Key endpoints after starting services:**
- **API entrypoint**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 3. Optional: Start Reference Frontend (when implemented):
```bash
cd frontend
npm install
npm start
```

## 🔧 Key Features

- **Headless API**: Complete REST API for content management.
- **Content Management**: Full CRUD operations for any content type.
- **Taxonomy System**: Categories and tags with flexible filtering.
- **User Management**: Role-based access (ADMIN, EDITOR).
- **Security**: Spring Security with configurable authentication.
- **Performance**: High-performance caching with Caffeine.
- **Rate Limiting**: IP-based protection with Bucket4j.
- **Multi-Database Support**: Works with MySQL 8.0 and PostgreSQL 12+.
- **CI/CD**: GitHub Actions pipeline with automated testing.

## 📚 Documentation

Comprehensive documentation covering installation, development, API usage, and deployment.

- **[📖 English Documentation](docs/en/)** - Complete guides and technical specifications.
- **[📖 Русская документация](docs/ru/)** - Полная документация на русском языке.

## 🌟 Use Cases

### For Development Teams & Agencies
- Build custom frontends with any modern framework (React, Vue, Angular, etc.).
- Integrate content into existing mobile applications.
- Power digital experiences, from websites to IoT devices.
- Connect analytics and advertising platforms seamlessly.

### For Businesses & Organizations
- Use the reference frontend for a quick and robust website deployment.
- Customize the provided Angular template to match your brand.
- Scale up to custom solutions as your organization grows.

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Security
- **Database**: MySQL 8.0, PostgreSQL 12+ (H2 for testing)
- **Caching**: Caffeine
- **Rate Limiting**: Bucket4j
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, TestContainers
- **CI/CD**: GitHub Actions
- **Reference Frontend**: Angular (planned)

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.

## Legal Notice

This project is distributed under the MIT License.  
All source code is authored and reviewed by Roman Konoplev. Some parts may have been assisted by AI tools,
but all code has been verified to comply with open-source licensing standards.

---

**Phoebe CMS** - Empowering modern development with headless flexibility and professional-grade features.