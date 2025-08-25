-- Migration V3: Insert sample data for testing News Platform APIs
-- Provides initial roles, users, taxonomy term, and a sample news article.
-- Location: backend/src/main/resources/db/migration/V3__insert_sample_data.sql

-- Insert roles
INSERT INTO roles (id, name) VALUES
  (1, 'ADMIN'),
  (2, 'EDITOR')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Insert sample user
INSERT INTO users (id, username, email, status) VALUES
  (100, 'admin', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Insert taxonomy category
INSERT INTO terms (id, name, vocabulary) VALUES
  (200, 'General', 'category')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Insert news article authored by user 100 in category 200
INSERT INTO content (id, title, body, teaser, publication_date, author_id,
                     created_at, updated_at, published)
VALUES
  (300, 'Hello World News',
   'This is the body of the first article.',
   'This is the teaser/summary.',
   NOW(), 100, NOW(), NOW(), 1)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- Link news 300 with category 200
INSERT INTO content_terms (content_id, term_id) VALUES
  (300, 200)
ON DUPLICATE KEY UPDATE term_id = VALUES(term_id);