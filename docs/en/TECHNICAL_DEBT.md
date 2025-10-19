# Known Issues & Technical Debt

This document tracks the technical debt, known issues, and future improvements for the Phoebe CMS project.

---

## ✅ Resolved

- ~~Code duplication in mappers~~ → Implemented BaseMapper
- ~~Missing permissions system~~ → Added permissions system with V5/V6 migrations
- ~~Inconsistent error handling~~ → Global exception handler implemented
- ~~Missing database migration docs~~ → DATABASE_GUIDE.md created
- ~~CMS vs MySQL password confusion~~ → Documentation clarified

---

## 🔄 In Progress

- **Reference Frontend Templates (Angular & Next.js)**: Basic structures created, require component implementation.
- **API Performance**: Query optimization for high-traffic scenarios.

---

## 🎯 Future Technical Debt

This section outlines planned features and improvements, categorized by priority.

### Security (High Priority)

- **OAuth 2.0 + JWT**: Replace Basic Auth with modern, token-based authentication for enhanced security.
- **2FA for ADMIN/EDITOR**: Implement two-factor authentication for critical user roles.
- **Production CORS**: Fine-tune Cross-Origin Resource Sharing (CORS) configuration for multi-domain frontends in a production environment.
- **User-based Rate Limiting**: Supplement the current IP-based restrictions with user-specific limits for authenticated users.

### Functionality (Medium Priority)

- **File Upload**: Implement support for uploading and managing images and other media directly through the API.
- **Advanced Search**: Integrate a full-text search engine like Elasticsearch or Lucene for more powerful search capabilities.
- **Webhooks**: Provide a system for sending event-driven notifications to external services (e.g., on content creation or update).
- **Content Versioning**: Implement a system to track and revert changes to articles, providing a complete history.
- **Publication Scheduler**: Allow users to schedule content to be published at a future date and time.

### Performance (Medium Priority)

- **Distributed Caching**: Integrate a distributed cache like Redis to support multi-instance deployments and improve performance.
- **Database Optimization**: Add and optimize database indexes for complex and frequently used queries.
- **Cursor Pagination**: Implement cursor-based pagination for more efficient navigation of very large datasets.
- **CDN Integration**: Provide guidance and support for integrating with a Content Delivery Network (CDN) for static resources.

### DevOps (Low Priority)

- **Kubernetes Manifests**: Create and maintain Kubernetes manifests for easier cloud-native deployments.
- **Monitoring**: Integrate with Prometheus and Grafana to provide detailed application and system metrics.
- **Structured Logging**: Implement structured logging (e.g., with the ELK stack) for more efficient log analysis and monitoring.
- **Backup Automation**: Develop and document a strategy for automated, regular database backups.
