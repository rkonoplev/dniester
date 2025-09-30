-- =============================================================================
-- init_admin.sql
-- This script creates a default admin user for LOCAL DEVELOPMENT ONLY.
-- DO NOT USE this script in production environments.
-- Password is 'admin' (BCrypt hash with strength 12).
-- =============================================================================

-- Step 1: Ensure the ADMIN role exists
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN');

-- Step 2: Create the default admin user
-- WARNING: Replace the password hash with a real BCrypt hash of 'admin'
INSERT INTO users (id, username, password, email, active)
VALUES (
    1,
    'admin',
    '$2a$12$REAL_BCRYPT_HASH_HERE', -- ⚠️ MUST BE REPLACED WITH A REAL HASH
    'admin@example.com',
    true
);

-- Step 3: Assign the ADMIN role to the user
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);