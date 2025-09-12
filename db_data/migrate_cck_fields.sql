-- ============================================================
-- Migration script: CCK custom fields → custom_fields table
-- Purpose:
--   * Scans all Drupal6 "content_type_*" tables
--   * For each field copies values into clean custom_fields
-- ============================================================

-- Generate SQL statements automatically inside MySQL with:
-- (this should be run manually in MySQL shell to inspect all CCK fields)
-- ============================================================
-- SELECT CONCAT(
--   'INSERT INTO custom_fields (content_id, field_name, field_value) ',
--   'SELECT nid, ''', c.COLUMN_NAME, ''', ', c.COLUMN_NAME,
--   ' FROM ', c.TABLE_SCHEMA, '.', c.TABLE_NAME,
--   ' WHERE ', c.COLUMN_NAME, ' IS NOT NULL;'
-- ) AS sql_statement
-- FROM information_schema.COLUMNS c
-- WHERE c.TABLE_SCHEMA = 'a264971_dniester'
--   AND c.TABLE_NAME LIKE 'content_type_%'
--   AND c.COLUMN_NAME NOT IN ('nid','vid');
-- ============================================================
--
-- This query will output prepared INSERTs for each CCK field.
-- Copy them below and run them.

-- === Example for table content_type_article ===
-- Sample field: field_subtitle_value
INSERT INTO custom_fields (content_id, field_name, field_value)
SELECT nid, 'field_subtitle_value', field_subtitle_value
FROM a264971_dniester.content_type_article
WHERE field_subtitle_value IS NOT NULL;

-- Sample field: field_image_fid
INSERT INTO custom_fields (content_id, field_name, field_value)
SELECT nid, 'field_image_fid', field_image_fid
FROM a264971_dniester.content_type_article
WHERE field_image_fid IS NOT NULL;

-- Add similar INSERTs for all other fields & content_type_* tables