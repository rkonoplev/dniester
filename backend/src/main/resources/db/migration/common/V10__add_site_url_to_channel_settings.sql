-- V10: Add site_url field to channel_settings table
-- This field stores the base URL for the site (e.g., https://dniester.ru)

ALTER TABLE channel_settings ADD COLUMN site_url VARCHAR(255);

-- Set default value for existing record
UPDATE channel_settings SET site_url = 'https://localhost:8080' WHERE id = 1;