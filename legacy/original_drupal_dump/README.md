# Archive of the Original Drupal 6 Database Dump

**WARNING:** The files in this directory are a historical archive. They are **not used** in the current project workflow.

[Русская версия](./README_RU.md)

---

### Description

This folder contains the original ("raw") database dump from the Drupal 6 site. This dump represents **"Point A"** — the initial state of the data before the migration process to the new platform began.

### Purpose

- **Historical Value:** Allows for an understanding of what the data looked like at the very beginning.
- **Debugging:** Can be useful if questions about the correctness of data transfer arise in the future. The final data can be compared with this original source.

### Files

- **`drupal6_working.sql`**
  - **Git Status:** ❌ **Not tracked** and added to `.gitignore`.
  - **Reason:** Large file size (over 200 MB). Storing such large files in a Git repository is impractical and blocked by GitHub's limits. The file is kept locally for historical reference only.
