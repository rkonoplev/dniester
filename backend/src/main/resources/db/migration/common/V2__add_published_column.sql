-- Migration V2: Add "published" column to content table
-- Purpose: Enable publish/unpublish workflow for News entity.
-- Location: backend/src/main/resources/db/migration/V2__add_published_column.sql

ALTER TABLE content
    ADD COLUMN published TINYINT(1) NOT NULL DEFAULT 0
    COMMENT 'Publication flag: 0=draft/unpublished, 1=published';