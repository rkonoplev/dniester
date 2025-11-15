# Frontend Technical Specification (Angular)

> **Note**: This document describes a **planned** reference implementation.
> For details on the **existing** implementation, please refer to the
> **[Next.js Specification](./FRONTEND_SPEC_NEXTJS.md)**.

## 1. General Objectives
- Develop a **modern, responsive news portal frontend** using Angular.
- Follow **Google News–inspired design principles**: clean, spacious, mobile-first,
  and highly readable.
- Provide an **SEO-friendly structure** through Server-Side Rendering (SSR).
- Ensure **accessibility (WCAG 2.1 AA)** and **fast loading performance**.

---

## 2. Technology Stack
- **Framework:** Angular (utilizing Angular Universal for SSR).
- **UI Library:** Angular Material.
- **Styling:** Custom Angular Material theme for branding, typography, and colors.
- **State Management:** Services with RxJS for state management and data caching.
- **Typography:** Roboto (base) + Roboto Slab / Serif for article headlines.

---

## 3. Architecture and Structure

The project will be structured using Angular's modular approach for better organization.

- **`AppModule`**: The root module.
- **`CoreModule`**: Singleton services (e.g., `ApiService`, `AuthService`).
- **`SharedModule`**: Common components, directives, and pipes (e.g., `LayoutComponent`).
- **`PublicModule`**: A feature module for public-facing pages (home, article, category).
- **`AdminModule`**: A lazy-loaded feature module for the admin panel.

---

## 4. Layout & Pages

### 4.1 Homepage
- Clean multi-column grid layout.
- Featured article block at the top (large image, headline, teaser).
- Secondary articles below in compact card format.
- Category navigation bar at the top.

### 4.2 Category Page
- Paginated list of articles belonging to the selected category.
- Category title highlighted with a branding accent color.

### 4.3 Article Page
- Large headline with a serif font.
- Featured image (responsive, up to ~800px wide).
- Publication date, author, and category.
- Full `body` and `teaser` content rendered as HTML to support
  *allowed* embedded HTML elements (without direct image and video embedding).
- Related articles at the bottom.
- SEO metadata, OpenGraph, and Twitter cards generated on the server.

### 4.4 Technical and Informational Pages
- Pages like "About Us," "Contact," and "Privacy Policy" will be implemented as
  standard "nodes" (articles) in the CMS, assigned a special taxonomy term
  (e.g., "technical_page").
- Links to these pages will be dynamically managed via the channel settings.

---

## 5. Functionality

### 5.1 SEO & Rendering
- **Server-Side Rendering (SSR) with Angular Universal**: All public pages will be
  pre-rendered on the server. This ensures maximum performance on the first load and
  guarantees that search engine crawlers receive fully-formed HTML for indexing.
- **Dynamic Meta Tags**: Use Angular's `Meta` and `Title` services to dynamically
  set `<title>`, `<meta name="description">`, and OpenGraph tags on the server for
  each page.
- **Static URLs**: Static, human-readable URLs for articles (format `/news/{slug}-{id}`,
  e.g., `/news/article-title-15378`) and categories (`/category/{slug}-{id}`).

### 5.2 Global Site Settings Management
- **"Channel Settings" Page**: A dedicated page will be created in the admin panel
  to manage global site settings (the `ChannelSettings` entity).
- **Editable Fields**:
  - `siteTitle`: The site name for the `<title>` tag.
  - `metaDescription`, `metaKeywords`: Global meta tags.
  - `headerHtml`: An editable HTML block for the site header.
  - `footerHtml`: An editable HTML block for the footer, allowing an admin to
    manage links, copyright, and logo.
  - `logoUrl`: URL for the logo.
  - `mainMenuTermIds`: A list of term IDs to form the main menu.
- **Integration**: The `LayoutComponent` will fetch these settings via the API and
  dynamically render the header, footer, and menu.

### 5.3 Admin Panel
- Will be implemented similarly to the Next.js version, including:
  - Role-based authentication and authorization (ADMIN/EDITOR).
  - CRUD operations for news, terms, and users (user management for ADMIN only).
  - Frontend validation for all forms.
  - A **WYSIWYG editor** for the `body` and `teaser` fields, with the ability to
    insert links and *a limited* set of HTML tags (without direct image and video embedding).

---

## 6. Future Enhancements (Optional)
- Full-text search.
- Dark mode theme toggle.
- Lazy loading for images (`ng-lazyload-image` or similar).
- Push notifications for breaking news.
- **File upload capabilities for images and other media**: This is directly related to secure media embedding. Implementing this functionality will require backend updates for secure handling and storage of uploaded files, as well as updates to HTML sanitization rules.

---

## Implementation Status

**🚧 NOT STARTED** - Development of the Angular frontend version has not yet begun. This document is a specification for future implementation.
