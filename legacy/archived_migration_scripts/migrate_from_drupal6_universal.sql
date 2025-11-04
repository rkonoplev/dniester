-- ============================================================
-- Universal migration script: Drupal 6 → Clean schema
-- Features:
--   * All tables created with UTF8
--   * No "type" field in content (all nodes = unified content)
--   * Users, roles, taxonomy preserved
--   * Custom CCK fields will be handled separately in migrate_cck_fields.sql
-- ============================================================

-- Disable FK checks for DROP
SET FOREIGN_KEY_CHECKS = 0;

-- 0. Drop new schema tables if they already exist
DROP TABLE IF EXISTS content_terms;
DROP TABLE IF EXISTS terms;
DROP TABLE IF EXISTS custom_fields;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS content;

-- Enable FK checks back
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS
CREATE TABLE users (
  id INT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO users (id, username, email, password, active)
SELECT 
  uid, 
  name, 
  COALESCE(NULLIF(mail, ''), CONCAT('user', uid, '@migrated.local')),
  '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', -- BCrypt 'admin'
  CASE WHEN status = 1 THEN true ELSE false END
FROM a264971_dniester.users
WHERE uid > 0;

-- 2. ROLES + USER_ROLES
CREATE TABLE roles (
  id INT PRIMARY KEY,
  name VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE user_roles (
  user_id INT,
  role_id INT,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO roles (id, name)
SELECT rid, name FROM a264971_dniester.role;

INSERT INTO user_roles (user_id, role_id)
SELECT uid, rid FROM a264971_dniester.users_roles;

-- 3. CONTENT (universal)
CREATE TABLE content (
  id INT PRIMARY KEY,
  title VARCHAR(50) NOT NULL,
  body LONGTEXT,
  teaser TEXT,
  publication_date DATETIME NOT NULL,
  published BOOLEAN NOT NULL DEFAULT false,
  created_at DATETIME,
  updated_at DATETIME,
  version BIGINT,
  author_id INT NOT NULL,
  FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO content (id, title, body, teaser, publication_date, published, created_at, updated_at, version, author_id)
SELECT
  n.nid,
  LEFT(n.title, 50),
  nr.body,
  LEFT(nr.teaser, 250),
  FROM_UNIXTIME(n.created),
  CASE WHEN n.status = 1 THEN true ELSE false END,
  FROM_UNIXTIME(n.created),
  FROM_UNIXTIME(n.changed),
  1,
  n.uid
FROM a264971_dniester.node n
LEFT JOIN a264971_dniester.node_revisions nr ON n.vid = nr.vid
WHERE n.uid IS NOT NULL;

-- 4. TAXONOMY
CREATE TABLE terms (
  id INT PRIMARY KEY,
  name VARCHAR(255),
  vocabulary VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE content_terms (
  content_id INT,
  term_id INT,
  PRIMARY KEY (content_id, term_id),
  FOREIGN KEY (content_id) REFERENCES content(id),
  FOREIGN KEY (term_id) REFERENCES terms(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO terms (id, name, vocabulary)
SELECT td.tid, td.name, v.name
FROM a264971_dniester.term_data td
LEFT JOIN a264971_dniester.vocabulary v ON td.vid = v.vid;

INSERT INTO content_terms (content_id, term_id)
SELECT tn.nid, tn.tid
FROM a264971_dniester.term_node tn;

-- 5. CUSTOM FIELDS placeholder
CREATE TABLE custom_fields (
  id INT AUTO_INCREMENT PRIMARY KEY,
  content_id INT,
  field_name VARCHAR(255),
  field_value TEXT,
  FOREIGN KEY (content_id) REFERENCES content(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- At this stage, custom_fields is empty.
-- Use migrate_cck_fields.sql to populate from CCK tables.