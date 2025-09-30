-- WARNING: This file is for LOCAL DEVELOPMENT ONLY.
-- DO NOT USE in production environments.
-- Password is 'admin' (BCrypt hash).
-- Step 1: Ensure ADMIN role exists
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN');

-- Step 2: Create admin user
-- Replace the password hash with a real BCrypt hash of 'admin'
INSERT INTO users (id, username, password, email, active)
VALUES (
    1,
    'admin',
    '$2a$12$REAL_BCRYPT_HASH_HERE', -- ⚠️ Generate this via BCryptPasswordEncoder
    'admin@example.com',
    true
);

-- Step 3: Assign ADMIN role
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);