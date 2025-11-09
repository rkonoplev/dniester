-- V9: Add indexes and constraints with deduplication
-- [Deduplication] Clean up potential duplicates before adding unique constraints.

-- Deduplication for content_terms
CREATE TABLE content_terms_new LIKE content_terms;
INSERT INTO content_terms_new (content_id, term_id)
SELECT content_id, term_id
FROM content_terms
GROUP BY content_id, term_id;
RENAME TABLE content_terms TO content_terms_old, content_terms_new TO content_terms;
DROP TABLE content_terms_old;

-- Deduplication for user_roles
CREATE TABLE user_roles_new LIKE user_roles;
INSERT INTO user_roles_new (user_id, role_id)
SELECT user_id, role_id
FROM user_roles
GROUP BY user_id, role_id;
RENAME TABLE user_roles TO user_roles_old, user_roles_new TO user_roles;
DROP TABLE user_roles_old;

-- Deduplication for role_permissions
CREATE TABLE role_permissions_new LIKE role_permissions;
INSERT INTO role_permissions_new (role_id, permission_id)
SELECT role_id, permission_id
FROM role_permissions
GROUP BY role_id, permission_id;
RENAME TABLE role_permissions TO role_permissions_old, role_permissions_new TO role_permissions;
DROP TABLE role_permissions_old;

-- [Original V7] Add indexes and unique constraints

-- terms: index for vocabulary lookups
CREATE INDEX idx_term_vocabulary ON terms (vocabulary);

-- content_terms: unique pair and helpful indexes
ALTER TABLE content_terms
  ADD CONSTRAINT uq_content_terms UNIQUE (content_id, term_id);
CREATE INDEX idx_content_terms_content ON content_terms (content_id);
CREATE INDEX idx_content_terms_term ON content_terms (term_id);

-- user_roles: unique pair and helpful indexes
ALTER TABLE user_roles
  ADD CONSTRAINT uq_user_roles UNIQUE (user_id, role_id);
CREATE INDEX idx_user_roles_user ON user_roles (user_id);
CREATE INDEX idx_user_roles_role ON user_roles (role_id);

-- role_permissions: unique pair and helpful indexes
ALTER TABLE role_permissions
  ADD CONSTRAINT uq_role_permissions UNIQUE (role_id, permission_id);
CREATE INDEX idx_role_permissions_role ON role_permissions (role_id);
CREATE INDEX idx_role_permissions_perm ON role_permissions (permission_id);
