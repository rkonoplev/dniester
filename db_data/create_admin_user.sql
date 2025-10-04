-- WARNING: This file is for LOCAL DEVELOPMENT ONLY.
-- DO NOT USE in production environments.
-- Password is 'admin' (BCrypt hash).
-- Step 1: Ensure ADMIN role exists
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN');

-- Step 2: Create admin user
-- Password: 'admin' (BCrypt hash with strength 12)
INSERT INTO users (id, username, password, email, active)
VALUES (
    1,
    'admin',
    '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', -- BCrypt hash of 'admin'
    'admin@example.com',
    true
);

-- Step 3: Assign ADMIN role
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);