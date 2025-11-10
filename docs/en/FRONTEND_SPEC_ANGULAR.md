# Frontend Technical Specification (Angular)

## 1. General Objectives
- Develop a **modern, responsive news portal frontend**.
- Follow **Google News–inspired design principles**: clean, spacious, mobile-first, highly readable.
- Provide a **SEO-friendly structure** with static URLs for all news articles.
- Ensure **accessibility (WCAG 2.1 AA)** and **fast loading performance**.

---

## 2. Technology Stack
- **Framework:** Angular with Angular Universal (supports SSR for SEO and performance).
- **UI Library:** Angular Material.
- **Styling:** Custom Angular Material theme (overrides for branding, typography, and colors).
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
- **Server-Side Rendering (SSR)**: Use Angular Universal to pre-render pages on the server. This ensures that
  search engine bots receive fully-formed HTML, allowing for immediate and reliable content indexing.
- **Static URLs**: Static, human-readable URLs for articles (format `/node/{id}`, e.g., `/node/15378`)
  and categories (`/category/{id}`).
- **Hydration**: After the initial server-rendered page is delivered, the client-side Angular application
  takes over, providing a seamless, interactive user experience without a full page reload.
- **Structured Data**: Implement JSON-LD (`NewsArticle` schema) to provide rich metadata to search engines,
  enhancing search result appearance (rich snippets).
- **Meta Tags**: Dynamically generate `<title>`, `<meta name="description">`, and OpenGraph tags for each page to
  ensure optimal sharing on social media and correct indexing.

### 4.2 Responsive Design
- Mobile-first layout.
- Adaptive typography and fluid images.

### 4.3 Accessibility
- WCAG 2.1 AA compliance.
- ARIA attributes, proper contrast, keyboard navigation.

### 4.4 Theming & Branding
- **Base colors**:
  - Primary: Dark Blue (#1c355e).
  - Secondary: Deep Red (#cc0000).
  - Background: White (#ffffff) or Off-White (#fdfcf8).
- **Usage**:
  - Headlines and links: Dark Blue.
  - Category highlights: Red.
  - Navigation bar: Dark Blue background with white text.
- **Typography**:
  - Headlines: Serif (Roboto Slab).
  - Body text: Sans-serif (Roboto).

---

## 5. Form Validation & User Experience

### 5.1 Channel Settings Form
- **URL Validation**: Add client-side validation for `siteUrl` field using Angular validators.
- **HTML Content Warnings**: Display clear warnings about HTML restrictions before users enter content.
- **Safe HTML Preview**: Show real-time preview of HTML content with only safe tags rendered.
- **Taxonomy Panel**: Use `mainMenuTermIds` field for storing JSON array of term IDs with proper validation.
- **Error Handling**: Provide user-friendly error messages for validation failures.

### 5.2 Input Validation Examples
```typescript
// URL validation
siteUrlControl = new FormControl('', [
  Validators.pattern(/^https?:\/\/.+/)
]);

// JSON validation for mainMenuTermIds
mainMenuTermIdsControl = new FormControl('', [
  this.jsonArrayValidator
]);

jsonArrayValidator(control: AbstractControl): ValidationErrors | null {
  try {
    const parsed = JSON.parse(control.value);
    return Array.isArray(parsed) ? null : { invalidJson: true };
  } catch {
    return { invalidJson: true };
  }
}
```

---

## 6. Future Enhancements (Optional)
- Full-text search with auto-suggestions.
- Dark mode theme toggle.
- Lazy loading for images and infinite scroll on category pages.
- Push notifications for breaking news.
