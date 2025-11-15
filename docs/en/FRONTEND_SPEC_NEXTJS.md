# Frontend Technical Specification (Next.js)

> **Current Implementation Details**: For a detailed overview of the implemented features,
> setup instructions, and further development plans, please refer to the
> **[Next.js Frontend README](../../frontends/nextjs/README.md)**.
>
> **Implementation Status**: 🚧 **IN ACTIVE DEVELOPMENT** - The frontend is currently under active development.

## 1. General Objectives
- Develop a **modern, responsive news portal frontend**.
- Follow **Google News–inspired design principles**: clean, spacious, mobile-first,
  highly readable.
- Provide a **SEO-friendly structure** with static URLs for all news articles.
- Ensure **accessibility (WCAG 2.1 AA)** and **fast loading performance**.

---

## 2. Technology Stack
- **Framework:** Next.js (React, supports SSR/SSG for SEO and performance).
- **UI Library:** Material UI (MUI).
- **Styling:** Custom Material theme (overrides for branding, typography, and colors).
- **Typography:** Roboto (base) + Roboto Slab / Serif for article headlines.

---

## 3. Layout & Pages
### 3.1 Homepage
- Clean multi-column grid layout:
  - Desktop: 2–3 columns.
  - Mobile: 1 column.
- Featured article block at the top (large image, headline, excerpt).
- Secondary articles below with medium-sized images.
- Smaller news items listed in compact cards.
- Category navigation bar at the top.

### 3.2 Category Page
- Paginated list of articles belonging to the selected category.
- Category title highlighted with branding accent color.

### 3.3 Article Page
- Large headline with serif font.
- Featured image (responsive, up to ~800px wide).
- Publication date, author, and category.
- Full content with images and inline HTML elements.
- Related articles at the bottom.
- SEO metadata, OpenGraph, and Twitter card support.

### 3.4 Static Pages
- About page.
- Archive page (filter by date/category).

---

## 4. Functionality
### 4.1 SEO & URLs
- **Hybrid Rendering Strategy**: Utilize Next.js's rendering capabilities to maximize
  performance and SEO.
  - **Server-Side Rendering (SSR)**: For dynamic pages like the homepage and category
    listings, ensuring content is always fresh and immediately indexable.
  - **Static Site Generation (SSG)**: For individual article pages and static pages
    (`About`, `Contact`), providing the fastest possible load times.
  - **Incremental Static Regeneration (ISR)**: Optionally use ISR to rebuild static
    pages in the background at a set interval, combining the speed of static with the
    freshness of dynamic content.
- **Static URLs**: Static, human-readable URLs for articles (format `/news/{slug}-{id}`,
  e.g., `/news/article-title-15378`) and categories (`/category/{slug}-{id}`).
- **Structured Data**: Implement JSON-LD (`NewsArticle` schema) to provide rich
  metadata to search engines, enhancing search result appearance (rich snippets).
- **Meta Tags**: Dynamically generate `<title>`, `<meta name="description">`, and
  OpenGraph tags for each page to ensure optimal sharing on social media and correct
  indexing.

### 4.2 Responsive Design
- Mobile-first layout.
- Adaptive typography and fluid images.
- Recommended image sizes:
  - Top article: ~600–800px width.
  - Standard articles: ~320–400px width.
  - Thumbnails: ~160–200px width.

### 4.3 Accessibility
- WCAG 2.1 AA compliance.
- ARIA labels, proper contrast, keyboard navigation.

### 4.4 Theming & Branding
- **Base colors**:
  - Primary: Dark Blue (#1c355e).
  - Secondary: Deep Red (#cc0000).
  - Background: White (#ffffff) or Off-White (#fdfcf8).
  - Neutral gray accents for borders and dividers (#eeeeee).
- **Usage**:
  - Headlines and links: Dark Blue.
  - Category highlights (e.g., Analytics): Red.
  - Navigation bar: Dark Blue background with white text.
- **Typography**:
  - Headlines: Serif (Roboto Slab).
  - Body text: Sans-serif (Roboto).

---

## 5. Implemented Features

### 5.1 Core Setup & Infrastructure
- **Project Setup**: Next.js with React, Material UI (MUI), and Axios.
- **Docker Integration**: Dockerfile for Next.js app and updated docker-compose.yml
  with nextjs-app service on port 3000.
- **Environment Variables**: Configuration for backend API base URL (.env.local).

### 5.2 User Interface & Theming
- **Custom Material UI Theme**: Light and dark mode support with toggle in footer.
- **Global Layout Component**: Consistent header, main content area, and footer.
- **Custom 404 Page**: For unhandled routes.

### 5.3 Authentication & Authorization
- **AuthContext**: Global management of authentication status and roles (ADMIN/EDITOR).
- **Login Page** (/admin/login): With frontend validation for username and password.
- **Protected Routes**: ProtectedRoute and AdminRoute components for access control.
- **Dynamic AdminBar**: Displayed only for authenticated users.

### 5.4 Public Content Pages
- **Homepage** (/): SSR display of latest news articles.
- **Article Detail Page** (/node/[id]): SSG with ISR for optimal performance and SEO.
- **Category Page** (/category/[id]): SSR list of articles for specific category.
- **Search Page** (/search): Search form integrated with backend API.
- **Informational Pages**: Placeholder pages for footer links.

### 5.5 Admin Panel (CRUD Operations)
- **News Management**: List, create, edit, and delete articles.
- **Taxonomy Management**: Full CRUD for terms and categories.
- **User Management**: View and edit user roles (ADMIN only).
- **Frontend Validation**: Comprehensive form validation according to VALIDATION_GUIDE.md.

### 5.6 Content Handling
- **HTML Rendering**: Support for HTML content (allowed tags only) and YouTube embeds in teaser and body fields.
- **Safe Processing**: Using backend's SafeHtml processing.

---

## 6. Future Enhancements (Optional)
- Full-text search with auto-suggestions (e.g., using Pagefind).
- Lazy loading for images and infinite scroll on category pages.
- Push notifications for breaking news.
- OAuth 2.0 + JWT integration for enhanced authentication security.
- **File upload capabilities for images and other media**: This is directly related to secure media embedding. Implementing this functionality will require backend updates for secure handling and storage of uploaded files, as well as updates to HTML sanitization rules.

---

## Running the Next.js Frontend Locally

To run the Next.js frontend on your local machine:

1.  **Ensure the backend is running**:
    ```bash
    cd phoebe # Project root directory
    make run # This will start the backend and database
    ```
    Verify that the backend is accessible at `http://localhost:8080`.

2.  **Navigate to the Next.js frontend directory**:
    ```bash
    cd frontends/nextjs
    ```

3.  **Install dependencies**:
    ```bash
    npm install
    ```

4.  **Start the Next.js application**:
    ```bash
    npm run dev
    ```
    The application will be available at `http://localhost:3000`.

5.  **View in browser**: Open `http://localhost:3000` in your web browser.
    You should see the Next.js frontend homepage displaying content from the locally running backend.
