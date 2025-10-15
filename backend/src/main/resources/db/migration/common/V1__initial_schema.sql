-- V1: Initial Schema Setup
-- This script creates the core tables required for the application to run.

-- Roles table for user authorization (e.g., ADMIN, EDITOR)
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE = InnoDB;

-- Users table for storing user accounts
CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) UNIQUE,
    active   BOOLEAN      NOT NULL DEFAULT TRUE
) ENGINE = InnoDB;

-- Join table for the many-to-many relationship between users and roles
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Terms table for taxonomy (categories, tags)
CREATE TABLE IF NOT EXISTS terms
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    vocabulary VARCHAR(100),
    UNIQUE (name, vocabulary)
) ENGINE = InnoDB;

-- Content table for news articles (maps to the News entity)
CREATE TABLE IF NOT EXISTS content
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    body             TEXT,
    teaser           TEXT,
    publication_date DATETIME     NOT NULL,
    published        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       DATETIME,
    updated_at       DATETIME,
    version          BIGINT,
    author_id        BIGINT       NOT NULL,
    INDEX idx_news_title (title),
    INDEX idx_news_published (published),
    INDEX idx_news_publication_date (publication_date),
    FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- Join table for the many-to-many relationship between content (news) and terms
CREATE TABLE IF NOT EXISTS content_terms
(
    content_id BIGINT NOT NULL,
    term_id    BIGINT NOT NULL,
    PRIMARY KEY (content_id, term_id),
    FOREIGN KEY (content_id) REFERENCES content (id) ON DELETE CASCADE,
    FOREIGN KEY (term_id) REFERENCES terms (id) ON DELETE CASCADE
) ENGINE = InnoDB;