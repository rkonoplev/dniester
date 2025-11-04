# Archived SQL Scripts for Migration Process

**WARNING:** The files in this directory are a historical archive. They are **not used** in the current project workflow and are preserved solely to document the one-time migration process from Drupal 6 to the new data schema.

The current project uses **Flyway** for automated migrations, located in `backend/src/main/resources/db/migration/common`.

[Русская версия](./README_RU.md)

---

### Role of These Scripts in the Migration Process

The migration was based on the ETL (Extract, Transform, Load) principle. These scripts performed the **Transform** step, converting raw data from a Drupal 6 dump into a clean, structured schema for the new application.

---

### File Descriptions

#### Core Transformation Scripts

- **`migrate_from_drupal6_universal.sql`**
  - **Git Status:** ✅ Tracked.
  - **Purpose:** The main, core migration script. It created new tables (`users`, `roles`, `content`, `terms`, etc.) and transferred data into them from old Drupal tables, cleaning and normalizing it along the way. This was the heart of the entire process.

- **`update_migrated_users.sql`**
  - **Git Status:** ✅ Tracked.
  - **Purpose:** A post-processing script for users. It was executed *after* the main migration script to set a temporary password (`changeme123`), generate temporary emails, etc.

#### Scripts for Custom Fields (CCK)

- **`detect_custom_fields.sql`**
  - **Git Status:** ✅ Tracked.
  - **Purpose:** A diagnostic script. It checked whether the old Drupal site used custom fields (the CCK module).

- **`migrate_cck_fields.sql`**
  - **Git Status:** ✅ Tracked.
  - **Purpose:** An optional script. If custom fields were found, this script would transfer their values into the new `custom_fields` table.

#### Helper and Final-State Files

- **`create_admin_user.sql`**
  - **Git Status:** ✅ Tracked.
  - **Purpose:** A helper script for local development. Its functionality is now fully covered by an automated Flyway migration.

- **`clean_schema.sql`**
  - **Git Status:** ❌ **Not tracked** and added to `.gitignore`.
  - **Reason:** Large file size. This was the final database dump *after* all transformations were applied. Its content served as the basis for the automated Flyway migrations.

- **`drupal6_fixed.sql`**
  - **Git Status:** ❌ **Not tracked** and added to `.gitignore`.
  - **Reason:** Large file size. This was an intermediate copy of the original Drupal dump used for the transformations.
