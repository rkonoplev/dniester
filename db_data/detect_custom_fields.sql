-- Detect CCK tables in Drupal 6 schema
SHOW TABLES LIKE 'content_type%';

-- For each table, list columns (custom fields)
-- Example:
SHOW COLUMNS FROM content_type_article;
SHOW COLUMNS FROM content_type_story;