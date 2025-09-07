# 🔧 Technical Specification for the Admin Panel

This document outlines the technical requirements for the News Platform admin panel interface.

---

## 1. Roles and Permissions

### 1.1 Role Management Page
- **View and edit roles** of administrators and editors
- **Pagination** for large user lists
- **Link to user creation page** for adding new authors or admins
- **User creation form** with login and password assignment for admin panel access

---

## 2. News Management

### 2.1 News List Page
- **Paginated list** of all news items
- **Action buttons** for each news item:
  - **Create News** - Navigate to creation form
  - **Edit News** - Navigate to editing form
  - **Delete News** - Remove news item with confirmation
  - **Unpublish** - Change publication status

---

## 3. Taxonomy Terms

### 3.1 Terms Management Page
- **Paginated list** of taxonomy terms
- **Add new terms** functionality
- **Delete existing terms** with confirmation
- **Term categories** management (e.g., categories, tags)

---

## 4. Bulk Actions

### 4.1 System-wide Bulk Operations
- **Bulk delete/unpublish** all site content (except main administrator)
- **Bulk role management** (delete all user roles except main administrator)

### 4.2 Content Filtering Bulk Operations
- **Bulk selection by term** - Select content matching specific taxonomy term
- **Bulk selection by author** - Select content by specific author
- **Confirmation step** required before final deletion for all bulk operations

---

## 5. News Creation/Editing Page

### 5.1 Form Fields
- **Headline** - Text input with validation
- **Teaser** - Textarea for article preview
- **News Body** - Rich text editor (WYSIWYG)
- **Term Selection** - Dropdown menu for single term assignment
- **Publication Date** - Auto-assigned on creation, format: "Publication Date: Thu 7 Sep 2017"

### 5.2 Rich Text Editor (WYSIWYG) Features
- **Text formatting buttons:**
  - Bold
  - Italic
  - Underline
  - Increase font size
  - Decrease font size
- **Hyperlink insertion** button
- **Media embedding** button for external images and videos

---

## 6. Security and Input Validation

### 6.1 Input Validation
- **Strict validation rules** for all input fields based on security best practices
- **Prevention of hacking techniques:**
  - SQL injection protection
  - XSS (Cross-Site Scripting) prevention
  - CSRF protection

### 6.2 Rich Text Editor Security
- **Allowed content:** Text formatting and external media embedding only
- **Content sanitization:** Strip or sanitize all HTML/script tags except allowed formatting
- **Whitelist approach** for permitted HTML tags and attributes

---

## 7. Technical Requirements

### 7.1 Pagination
- **All list pages** must include pagination functionality
- **Configurable page sizes** (10, 25, 50, 100 items per page)
- **Navigation controls** (Previous, Next, Page numbers)

### 7.2 User Experience
- **Confirmation dialogs** for destructive actions (delete, bulk operations)
- **Success/error notifications** for all operations
- **Responsive design** for tablet and desktop use
- **Keyboard navigation** support for accessibility

---