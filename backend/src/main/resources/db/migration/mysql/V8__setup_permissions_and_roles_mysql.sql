-- V8: Setup basic permissions and update existing roles with descriptions
-- This migration completes the permissions system setup

-- Update existing roles with descriptions
UPDATE roles SET description = 'System Administrator with full access' WHERE name = 'ADMIN';
UPDATE roles SET description = 'Content Editor with publishing rights' WHERE name = 'EDITOR';

-- Insert basic permissions
INSERT INTO permissions (name) VALUES 
('news:read'),
('news:create'),
('news:update'),
('news:delete'),
('news:publish'),
('users:read'),
('users:create'),
('users:update'),
('users:delete'),
('roles:read'),
('roles:create'),
('roles:update'),
('roles:delete'),
('terms:read'),
('terms:create'),
('terms:update'),
('terms:delete');

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.name = 'ADMIN';

-- Assign content-related permissions to EDITOR role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.name = 'EDITOR' 
AND p.name IN ('news:read', 'news:create', 'news:update', 'news:publish', 'terms:read');

-- Assign ADMIN role to existing admin user (ID 100)
INSERT IGNORE INTO user_roles (user_id, role_id) 
SELECT 100, id FROM roles WHERE name = 'ADMIN';
