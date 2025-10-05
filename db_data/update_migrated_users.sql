-- Post-migration script to update migrated users
-- Run this AFTER migrate_from_drupal6_universal.sql

-- 1. Update users without email to have valid email addresses
UPDATE users 
SET email = CONCAT('user', id, '@migrated.local') 
WHERE email LIKE '%@migrated.local';

-- 2. Set default password for all migrated users (they'll need to reset)
-- Password is 'changeme123' (BCrypt hash)
UPDATE users 
SET password = '$2a$12$rQWpvkUBtdLgNX9HlmEQaOeH4klaUzSbQ5/TgT.JJ9FXtqy4Qv5Gy'
WHERE password = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C';

-- 3. Ensure admin user exists with proper credentials
INSERT IGNORE INTO users (id, username, email, password, active) 
VALUES (999, 'admin', 'admin@phoebe.local', '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', true);

-- 4. Ensure ADMIN role exists and is assigned
INSERT IGNORE INTO roles (id, name, description) VALUES (999, 'ADMIN', 'System Administrator');
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (999, 999);

-- 5. Show migration summary
SELECT 
    COUNT(*) as total_users,
    SUM(CASE WHEN active = true THEN 1 ELSE 0 END) as active_users,
    SUM(CASE WHEN email LIKE '%@migrated.local' THEN 1 ELSE 0 END) as users_need_email_update
FROM users;