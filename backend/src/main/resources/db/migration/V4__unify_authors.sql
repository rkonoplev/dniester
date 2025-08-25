-- Migration V4: Unify all legacy authors into a single "ImportedAuthor"
-- Purpose: simplify migrated Drupal data -> every old article has the same author
-- Location: backend/src/main/resources/db/migration/V4__unify_authors.sql

-- 1. Insert or ensure a single "ImportedAuthor" user exists
INSERT INTO users (id, username, email, status)
VALUES (999, 'imported_author', 'imported@author.local', 1)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 2. Reassign all news records from any old author to the new ImportedAuthor
UPDATE content
SET author_id = 999;

-- 3. Optionally delete all legacy authors except the ImportedAuthor and your active staff accounts
-- ⚠️ Only do this if you are 100% sure you don't want to preserve old user data
-- DELETE FROM users WHERE id <> 999 AND id NOT IN (SELECT id FROM active_staff_users);

-- Note: Step 3 is commented out for safety - run manually if needed.