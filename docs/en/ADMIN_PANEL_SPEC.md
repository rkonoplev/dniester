> [Back to Documentation Contents](./README.md)

# Technical Specification for the Admin Panel

This document outlines the technical requirements for Phoebe CMS admin panel interface.

**[Detailed Next.js Frontend Implementation Description here](../../frontends/nextjs/README.md).**

---

## 1. Roles and Permissions

### 1.1 Role-Based Access Control

Phoebe CMS uses a two-role system: **ADMIN** (full access) and **EDITOR** (own content only).

**Implementation Details**: See [Role Security Implementation Guide](./ROLE_SECURITY_IMPLEMENTATION.md) for complete security requirements and code examples.

### 1.2 UI Security Enforcement
- **Role-based interface** - UI elements shown/hidden based on user role and content ownership
- **Real-time validation** - backend security checks prevent unauthorized operations

---

## 2. News Management

### 2.1 News List Page

#### For ADMIN Users
- **Paginated list** of all news items
- **Full action buttons** for each news item:
  - **Create News** - Navigate to creation form
  - **Edit News** - Navigate to editing form (any article)
  - **Delete News** - Remove any news item with confirmation
  - **Publish/Unpublish** - Change publication status (any article)

#### For EDITOR Users
- **Paginated list** of all news items (read-only view for others' content)
- **Limited action buttons** based on authorship:
  - **Create News** - Navigate to creation form
  - **Edit News** - Only available for own articles
  - **Delete News** - Only available for own articles with confirmation
  - **Publish/Unpublish** - Only available for own articles

---

## 3. Taxonomy Terms

### 3.1 Terms Management Page
- **Paginated list** of taxonomy terms
- **Add new terms** functionality
- **Delete existing terms** with confirmation
- **Term categories** management (e.g., categories, tags)

---

## 4. Bulk Actions

### 4.1 ADMIN-Only Bulk Operations
- **Bulk delete/unpublish** all site content
- **Bulk role management** (manage user roles)
- **System-wide operations** with confirmation dialogs
- **Bulk selection by term** - Select content matching specific taxonomy term
- **Bulk selection by author** - Select content by specific author

### 4.2 EDITOR Bulk Operations (RESTRICTED)
- **NO BULK OPERATIONS ALLOWED** - EDITOR role is restricted to single article operations only
- **Single article delete** - Can only delete one article at a time (own content)
- **Single article publish/unpublish** - Can only modify one article at a time (own content)
- **Security enforcement** - Backend prevents EDITOR from accessing bulk operation endpoints
- **UI restriction** - Bulk operation buttons hidden/disabled for EDITOR role

---

## 5. News Creation/Editing Page

### 5.1 Form Fields
- **Headline** - Text input with validation
- **Teaser** - Textarea for article preview
- **News Body** - Rich text editor (WYSIWYG)
- **Term Selection** - Dropdown menu for single term assignment
- **Publication Date** - Auto-assigned on creation but can be modified. Date format should conform to ISO-8601 (e.g., `2024-01-15T10:30:00`).

### 5.2 Rich Text Editor (WYSIWYG) Features
- **Text formatting buttons:**
  - Bold
  - Italic
  - Underline
  - Increase font size
  - Decrease font size
- **Hyperlink insertion** button
- **Media embedding** button for external images and videos.
*   **Important**: The backend **does not allow** embedding `<img>` and `<iframe>` tags in content fields due to security (XSS) concerns. The frontend should either not provide such functionality or warn the user that these tags will be removed upon saving.

---

## 6. Security and Input Validation

### 6.1 Input Validation
- **Strict validation rules** for all input fields based on security best practices
- **Prevention of hacking techniques:**
  - SQL injection protection
  - XSS (Cross-Site Scripting) prevention
- **CSRF Protection**: Disabled on the backend for the API (standard practice for RESTful APIs). The frontend should be designed with this in mind.

### 6.2 Rich Text Editor Security
- **Allowed content:** Text formatting and hyperlink insertion only.
- **Content sanitization:** Strip or sanitize all HTML/script tags except allowed formatting.
- **Whitelist approach** for permitted HTML tags and attributes.
*   **Note**: `<img>` and `<iframe>` tags are not included in the whitelist of allowed tags.

### 6.3 Authentication Security
- **Current authentication:** Spring Security with Basic Auth
- **Planned migration:** OAuth 2.0 + 2FA for all roles (ADMIN, EDITOR)
- **Password management:** Passwords are stored in the database in hashed form.
- **Admin access:** Controlled through application configuration (e.g., via Flyway migrations or manual creation), not user self-registration.

---

## 7. Technical Requirements

### 7.1 Pagination
- **All list pages** must include pagination functionality
- **Configurable page sizes** (10, 25, 50, 100 items per page) for selection in the UI. The backend supports a page size up to 100 items.
- **Navigation controls** (Previous, Next, Page numbers)

### 7.2 User Experience
- **Confirmation dialogs** for destructive actions (delete, bulk operations)
- **Success/error notifications** for all operations
- **Responsive design** for tablet and desktop use
- **Keyboard navigation** support for accessibility

---
