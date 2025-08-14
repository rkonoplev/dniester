# 🏗️ Architecture Migration & Git Preservation Guide

This document outlines the migration strategy for improving the project's architecture while preserving the legacy codebase for reference and rollback.

---

## 📌 Goals

- Minimal future intervention
- Low maintenance cost
- Stable operation on free-tier services
- Clean structure without complex patterns (no DDD)
- Safe Git history preservation and rollback capability

---

## 🚀 Migration Steps

### 1. Preserve Current `main` as Archive

```bash
git checkout main
git pull origin main
git branch main-legacy
git push origin main-legacy
```

> ✅ `main-legacy` now contains the original codebase before improvements.

---

### 2. Protect `main-legacy` on GitHub

Go to **GitHub → Settings → Branches → Add Rule**:

- Branch name pattern: `main-legacy`
- Enable:
    - ✅ Require pull request reviews
    - ✅ Include administrators
    - ✅ Restrict who can push (leave empty or add yourself)

> 🔒 This prevents accidental changes to the archived branch.

---

### 3. Create a Feature Branch for Improvements

```bash
git checkout -b feature/minimal-improvements
# Implement architecture improvements
git push -u origin feature/minimal-improvements
```

---

### 4. Replace `main` with Improved Version

After completing all improvements:

```bash
git checkout feature/minimal-improvements
git branch -m main
git push origin main --force
```

> ⚠️ This overwrites the old `main` with the new version. Safe because `main-legacy` is preserved.

---

## 🔁 Rollback Procedure (if needed)

To restore the legacy version:

```bash
git checkout main-legacy
git checkout -b main
git push origin main --force
```

---

## ✅ Summary

| Action | Command |
|--------|---------|
| Archive current `main` | `git branch main-legacy && git push origin main-legacy` |
| Protect archive branch | GitHub → Settings → Branch Protection |
| Create feature branch | `git checkout -b feature/minimal-improvements` |
| Replace `main` | `git branch -m main && git push origin main --force` |
| Rollback to legacy | `git checkout main-legacy && git checkout -b main && git push origin main --force` |

---

## 📘 Notes

- All improvements follow a clean, maintainable structure.
- Legacy code remains accessible and protected.
- Future development continues in the new `main` branch.

```
## Archived Branch: `main-legacy`

As of August 14, 2025, the `main-legacy` branch has been officially archived and locked for changes.  
It contains deprecated code and is preserved solely for historical reference.  
All active development now takes place in the `main` branch or other current branches.

🔗 Final commit before archival: [`8978e88`](https://github.com/rkonoplev/news-platform/commit/8978e8845a911aec1f2271e4e17f5013ef700efb)

