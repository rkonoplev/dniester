# Phoebe CMS - Next.js Frontend Reference Implementation

This directory contains the Next.js reference frontend for the Phoebe Headless CMS.

**[Русская версия документации](README_RU.md)**
It provides a modern, responsive news portal experience with a comprehensive admin panel.

## 1. Purpose and Goals

The primary goal of this frontend is to demonstrate a robust, SEO-friendly, and user-centric
application built on top of the Phoebe CMS backend. It follows Google News-inspired design
principles and implements key features for content consumption and management.

## 2. Features Implemented

### 2.1. Core Setup & Infrastructure
*   Next.js project initialized with React, Material UI (MUI), and Axios.
*   Docker integration: `Dockerfile` for the Next.js app and updated `docker-compose.yml`
    to include the `nextjs-app` service, accessible on port `3000`.
*   Environment variable configuration for the backend API base URL (`.env.local`).

### 2.2. User Interface & Theming
*   Custom Material UI theme with light and dark mode options, switchable via the footer.
    Theme colors align with `FRONTEND_SPEC_NEXTJS.md`.
*   Global `Layout` component providing consistent header, main content area, and footer.
*   Custom `404` Not Found page for unhandled routes.

### 2.3. Authentication & Authorization
*   `AuthContext` for global management of user authentication status and roles (ADMIN/EDITOR).
*   Login page (`/admin/login`) with robust frontend validation for username and password.
*   `ProtectedRoute` and `AdminRoute` components for role-based access control to admin sections.
*   Dynamic `AdminBar` displayed only for authenticated users, with navigation links
    tailored to their specific roles (e.g., "Taxonomy" and "Users" only for ADMINs).

### 2.4. Public-Facing Content Pages
*   **Homepage (`/`)**: Server-Side Rendered (SSR) display of the latest news articles.
*   **Article Detail Page (`/node/[id]`)**: Static Site Generation (SSG) with Incremental
    Static Regeneration (ISR) for optimal performance and SEO. Displays full article `body`.
*   **Category Page (`/category/[id]`)**: Server-Side Rendered (SSR) list of articles
    belonging to a specific category.
*   **Search Page (`/search`)**: Frontend search form integrated with the backend API.
    Results display article `teaser` content.
*   **Informational Pages**: Placeholder pages for footer links (e.g., Technical Sheet, Contact).

### 2.5. Admin Panel (CRUD Operations)
*   **News Management**:
    *   List all news articles (`/admin/news`) with pagination, edit, and delete options.
    *   Create new articles (`/admin/news/new`).
    *   Edit existing articles (`/admin/news/edit/[id]`).
    *   Frontend validation for `title` (5-50 chars), `teaser` (10-250 chars), and `body`
        (min 20 chars), aligned with backend rules.
    *   `teaser` and `body` fields support HTML content, images, and YouTube embeds.
*   **Taxonomy Management**:
    *   List all terms (`/admin/taxonomy`) with edit and delete options.
    *   Create new terms (`/admin/taxonomy/new`).
    *   Edit existing terms (`/admin/taxonomy/edit/[id]`).
    *   Frontend validation for `name` (2-100 chars), `vocabulary` (2-50 chars), and
        `description` (max 500 chars).
*   **User Management**:
    *   List all users (`/admin/users`).
    *   Edit user roles (`/admin/users/edit/[id]`) with frontend validation.

### 2.6. Validation & Content Handling
*   Comprehensive frontend validation implemented across all major forms to enhance UX
    and reduce server load, strictly adhering to the rules defined in `VALIDATION_GUIDE.md`.
*   `teaser` and `body` fields correctly render HTML content and YouTube embeds using
    `dangerouslySetInnerHTML`, leveraging the backend's `SafeHtml` processing.

## 3. Getting Started

### 3.1. Prerequisites
*   Node.js (v18+) and npm/yarn
*   Docker and Docker Compose
*   Phoebe CMS Backend running (as configured in `docker-compose.yml`)

### 3.2. Running the Application
1.  Navigate to the project root directory (`phoebe/`).
2.  Ensure your `.env.local` file in `frontends/nextjs/` is correctly configured
    (e.g., `NEXT_PUBLIC_API_BASE_URL=http://news-app:8080/api/public`).
3.  Start all services using Docker Compose:
    ```bash
    docker-compose up --build
    ```
4.  Access the Next.js frontend in your browser at `http://localhost:3000`.
5.  Access the admin login page at `http://localhost:3000/admin/login`.

## 4. Next Steps & Further Development
*   Implement advanced search functionality (e.g., using Pagefind for static content).
*   Integrate OAuth 2.0 + JWT for enhanced authentication security.
*   Add file upload capabilities for images and other media.
*   Refine UI/UX based on detailed design mockups.

## 5. Switching to an Alternative Frontend (e.g., Angular)

If you wish to use an alternative frontend (like the planned Angular version) and disable
this Next.js implementation, you would need to modify the main `docker-compose.yml` file.

1.  Open `phoebe/docker-compose.yml`.
2.  Locate and remove or comment out the `nextjs-app` service block:
    ```yaml
    # nextjs-app:
    #   build:
    #     context: ./frontends/nextjs
    #     dockerfile: Dockerfile
    #   container_name: nextjs-app
    #   restart: always
    #   depends_on:
    #     news-app:
    #       condition: service_healthy
    #   ports:
    #     - "3000:3000"
    #   volumes:
    #     - ./frontends/nextjs:/app
    #     - /app/node_modules
    #     - /app/.next
    ```
3.  Then, you would add the configuration for your alternative frontend service.
