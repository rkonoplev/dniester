# Database Schema – News Platform

This document describes the **final MySQL 8 database schema** used by the News Platform project.  
It has been normalized from the legacy Drupal 6 database into a modern schema compatible with **Spring Boot + JPA/Hibernate**.

The schema is designed to support:
- User & role management (migrated from Drupal),
- Unified `content` storage (articles, news, pages),
- Taxonomy system (categories/terms),
- Publish/unpublish workflow,
- Audit fields for tracking creation and modifications.

---

## Entity-Relationship (ER) Model – Overview

- **users**: System users imported from Drupal.
- **roles**: User roles (Admin, Editor, etc.).
- **user_roles**: Many-to-many mapping between `users` and `roles`.
- **content**: Unified table for all news/articles.
- **terms**: Taxonomy terms (categories, tags, vocabularies).
- **content_terms**: Many-to-many mapping between content and terms.

---

## DDL – Final Schema

```sql
-- ======================================
-- USERS
-- ======================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY,                       -- Drupal uid
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    status TINYINT(1) NOT NULL                   -- 1 = active, 0 = blocked
);

-- ======================================
-- ROLES
-- ======================================
CREATE TABLE roles (
    id BIGINT PRIMARY KEY,                       -- Drupal rid
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ======================================
-- USER_ROLES (link table, M:N)
-- ======================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ======================================
-- CONTENT (NEWS/ARTICLES)
-- ======================================
CREATE TABLE content (
    id BIGINT PRIMARY KEY,                       -- Drupal nid
    title VARCHAR(255) NOT NULL,
    body TEXT,                                   -- full article body
    teaser TEXT,                                 -- short summary
    publication_date DATETIME NOT NULL,          -- Drupal created → DATETIME

    author_id BIGINT,                            -- FK → users.id
    created_at DATETIME NOT NULL,                -- audit auto-set
    updated_at DATETIME NOT NULL,                -- audit auto-set
    published TINYINT(1) NOT NULL DEFAULT 0,     -- 0 = draft, 1 = published

    FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ======================================
-- TERMS (TAXONOMY)
-- ======================================
CREATE TABLE terms (
    id BIGINT PRIMARY KEY,                       -- Drupal tid
    name VARCHAR(255) NOT NULL,
    vocabulary VARCHAR(100)                      -- Drupal vocabulary name
);

-- ======================================
-- CONTENT_TERMS (link table, M:N content ↔ terms)
-- ======================================
CREATE TABLE content_terms (
    content_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    PRIMARY KEY(content_id, term_id),
    FOREIGN KEY(content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY(term_id) REFERENCES terms(id) ON DELETE CASCADE
);
```
## ER Diagram (ASCII Representation)

          +-------------------+
          |      users        |
          +-------------------+
          | id (PK)           |
          | username          |
          | email             |
          | status            |
          +-------------------+
                  | 1
                  |
                  | * (many news articles per user)
                  v
          +-------------------+
          |      content      |   (mapped to News entity)
          +-------------------+
          | id (PK)           |
          | title             |
          | body              |
          | teaser            |
          | publication_date  |
          | author_id (FK)    |
          | created_at        |
          | updated_at        |
          | published         |
          +-------------------+
                  ^
                  | *
                  | *
          +-------------------+
          |      terms        |
          +-------------------+
          | id (PK)           |
          | name              |
          | vocabulary        |
          +-------------------+

Relations:
users (1) ---- (*) content   → One user can author many articles.
content (*) ---- (*) terms   → Many-to-many relation via content_terms.


          +-------------------+
          |      roles        |
          +-------------------+
          | id (PK)           |
          | name              |
          +-------------------+
                  ^
                  | *
                  | *
          +-------------------+
          |   user_roles      |   Join table
          +-------------------+
          | user_id (FK)      |
          | role_id (FK)      |
          +-------------------+
                  ^
                  | *
                  | 1
          +-------------------+
          |      users        |
          +-------------------+


## Example Queries (cheatsheet)

```sql
-- list all published news articles
SELECT id, title, publication_date
FROM content
WHERE published = 1
ORDER BY publication_date DESC;

-- fetch all categories for one article
SELECT t.name
FROM terms t
JOIN content_terms ct ON t.id = ct.term_id
WHERE ct.content_id = 42;

-- list all active users and their roles
SELECT u.username, r.name AS role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.status = 1;
```

## Notes
- content is the main entity (News in JPA code).
- The `published` column (added in Flyway migration V2) implements explicit publish/unpublish workflow.
- Audit fields (created_at, updated_at) are managed automatically in JPA via entity lifecycle hooks.
- Categories/tags are stored in terms, linked via content_terms.


