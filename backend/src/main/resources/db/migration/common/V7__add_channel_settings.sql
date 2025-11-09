-- V7: Add channel settings table
-- Purpose: Store site-wide configuration like title, meta tags, and HTML snippets

CREATE TABLE channel_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_title VARCHAR(255),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    header_html TEXT,
    logo_url VARCHAR(500),
    footer_html TEXT,
    main_menu_term_ids TEXT
) ENGINE = InnoDB;

-- Insert default settings
INSERT INTO channel_settings (
    site_title, 
    meta_description, 
    meta_keywords, 
    logo_url, 
    main_menu_term_ids
) VALUES (
    'Phoebe CMS',
    'Modern headless CMS built with Spring Boot',
    'cms, headless, spring boot, java',
    '/logo.png',
    '[]'
);