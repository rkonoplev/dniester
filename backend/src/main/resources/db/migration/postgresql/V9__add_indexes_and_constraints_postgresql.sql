-- V9: Add indexes and constraints with deduplication
-- [Deduplication] Clean up potential duplicates before adding unique constraints.

-- Deduplication for content_terms
DELETE FROM content_terms a
USING content_terms b
WHERE a.ctid < b.ctid
  AND a.content_id = b.content_id
  AND a.term_id = b.term_id;

-- Deduplication for user_roles
DELETE FROM user_roles a
USING user_roles b
WHERE a.ctid < b.ctid
  AND a.user_id = b.user_id
  AND a.role_id = b.role_id;

-- Deduplication for role_permissions
DELETE FROM role_permissions a
USING role_permissions b
WHERE a.ctid < b.ctid
  AND a.role_id = b.role_id
  AND a.permission_id = b.permission_id;

-- [Original V7] Add indexes and unique constraints

-- terms: index for vocabulary lookups
CREATE INDEX IF NOT EXISTS idx_term_vocabulary ON terms (vocabulary);

-- terms: case-insensitive unique on (name, vocabulary)
CREATE UNIQUE INDEX IF NOT EXISTS uq_terms_name_vocab_ci
  ON terms (LOWER(name), LOWER(vocabulary));

-- content_terms: unique pair and helpful indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_content_terms
  ON content_terms (content_id, term_id);
CREATE INDEX IF NOT EXISTS idx_content_terms_content
  ON content_terms (content_id);
CREATE INDEX IF NOT EXISTS idx_content_terms_term
  ON content_terms (term_id);

-- user_roles: unique pair and helpful indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_user_roles
  ON user_roles (user_id, role_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user
  ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role
  ON user_roles (role_id);

-- role_permissions: unique pair and helpful indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_role_permissions
  ON role_permissions (role_id, permission_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role
  ON role_permissions (role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_perm
  ON role_permissions (permission_id);
