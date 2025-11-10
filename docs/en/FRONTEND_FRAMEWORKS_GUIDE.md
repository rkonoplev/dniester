# Frontend Frameworks Guide: A Comparative Overview

This document provides a theoretical and practical overview of the frontend frameworks used
and considered for the Phoebe CMS project, focusing on dependency size, project structure,
and development best practices.

---

## 1. Understanding `node_modules` and Dependency Size

A common observation in modern web development is the large size of the `node_modules`
directory. This is normal and expected. Let's break down why our Next.js project's
`node_modules` folder is approximately 350-400MB.

### 1.1. Key Dependencies and Their Impact

The size is primarily attributed to a few key dependencies that form the core of our stack:

*   **`@next` (112MB) & `next` (101MB)**: These packages contain the Next.js framework itself,
    including its compiler (SWC), development server, server-side rendering engine,
    static site generator, and various optimizations. The size reflects its comprehensive,
    "batteries-included" nature.
*   **`@mui` (27MB)**: Material-UI is a rich component library. This size includes not just
    the components but also styling engines, theming capabilities, and utility functions.
*   **`typescript` (23MB)**: The TypeScript compiler and its associated type definitions.
    Next.js has built-in TypeScript support, making this a standard dependency.
*   **Other Packages**: The remaining size is distributed among 320+ smaller packages,
    which include utilities, polyfills, and various other dependencies required by the
    main frameworks.

### 1.2. Why Was It Smaller Before?

A smaller `node_modules` size in the past could be due to several reasons:
*   **Incomplete Installation**: Not all dependencies were installed.
*   **Different Package Manager**: `yarn` or `pnpm` might create a different directory
    structure or use caching more aggressively than `npm`.
*   **Cache Clearing**: A recent cache clear (`npm cache clean --force`) could have forced
    a fresh download of all packages.

**Conclusion**: A `node_modules` size of 300-400MB is standard for a modern Next.js project
with a major UI library like Material-UI.

---

## 2. Framework Size Comparison: Next.js vs. Angular

While our current implementation uses Next.js, it's useful to compare it with Angular,
the other planned reference implementation.

| Framework         | Typical `node_modules` Size | Key Contributors                               |
|-------------------|-----------------------------|------------------------------------------------|
| **Next.js**       | **~350-400MB**              | Next.js framework, SWC, React, Material-UI     |
| **Angular**       | **~500-700MB**              | Angular CLI, DevKit, RxJS, Zone.js, Webpack    |
| **Vue.js**        | ~200-300MB                  | Vue core, Vite, smaller ecosystem              |
| **React (CRA)**   | ~250-350MB                  | React-scripts, Webpack, Babel                  |

### 2.1. Why is Angular "Heavier"?

Angular's larger size is a trade-off for its all-in-one, opinionated structure:

*   **Angular CLI & DevKit (~250MB+)**: A powerful set of tools for building, testing,
    and maintaining the application.
*   **RxJS (~15MB)**: A core dependency for handling asynchronous operations.
*   **Zone.js (~10MB)**: Manages change detection.
*   **Full Tooling Suite**: Angular comes with its own solutions for routing, forms,
    HTTP requests, and testing, all included by default.

**Conclusion**: Angular's larger `node_modules` size is the price for a fully integrated,
enterprise-grade framework where many architectural decisions are made for you.

---

## 3. Git, `node_modules`, and Best Practices

A critical rule in web development is to **never commit the `node_modules` directory to Git**.

### 3.1. What to Commit vs. What to Ignore

*   **✅ Commit to Git**:
    *   `package.json`: Lists all project dependencies.
    *   `package-lock.json`: "Locks" the exact versions of each dependency, ensuring
        consistent installations across all environments.
    *   Source code (`/pages`, `/components`, `/styles`, etc.).
    *   Configuration files (`next.config.js`, `tsconfig.json`).

*   **❌ Ignore in `.gitignore`**:
    *   `node_modules/`: All installed dependency packages.
    *   `.next/`: The output of the Next.js build process.
    *   `.env.local`: Local environment variables and secrets.
    *   IDE-specific files (`.vscode/`, `.idea/`).
    *   Log files (`*.log`).

### 3.2. The Workflow

This separation allows for an efficient and secure development process:

1.  A developer clones the repository (downloads only the source code, which is small).
    ```bash
    git clone <repository_url>
    ```
2.  They navigate into the project directory.
3.  They install the dependencies locally based on the `package-lock.json` file. This
    recreates the `node_modules` directory on their machine.
    ```bash
    npm install
    ```
4.  They start the development server.
    ```bash
    npm run dev
    ```

This practice saves repository space, speeds up cloning and CI/CD processes, and avoids
platform-specific issues.

### 3.3. Space-Saving Techniques

While `node_modules` should be large, you can manage disk space with these techniques:
*   **`npm ci`**: For CI/CD environments, this provides a faster, cleaner installation.
*   **`.dockerignore`**: Ensure `node_modules` is listed here to prevent it from being
    copied into your Docker image during the build.
*   **Multi-stage Docker Builds**: A common pattern where dependencies are installed in
    an intermediate "builder" stage, and only the final build artifacts are copied to
    the lean production image.
