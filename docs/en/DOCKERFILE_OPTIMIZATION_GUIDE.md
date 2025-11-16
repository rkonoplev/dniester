> [Back to Documentation Contents](./README.md)

# Dockerfile Optimization Guide for Frontend

This document covers two main approaches to creating a `Dockerfile` for a frontend application (`phoebe-nextjs`):
a simple single-stage file for development and debugging, and a more complex multi-stage file for optimization
and production.

## 1. Simplified Single-Stage Dockerfile

This option is ideal for quick debugging, initial setup, or for resolving build issues in an isolated environment.

```dockerfile
# Example of a simple Dockerfile
FROM node:20-alpine
WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./
RUN npm install

# Copy source code
COPY . .

# Build the application
RUN npm run build

EXPOSE 3000
CMD ["npm", "start"]
```

### Use Cases

*   **Quickly debugging build issues**: If the `make run` command fails during the frontend build stage, this
    `Dockerfile` helps isolate the problem. It removes the complexities of multi-stage builds and allows you to
    verify if the application builds in a clean environment.

*   **Initial setup for a new frontend service**: If you decide to add another frontend microservice, this
    `Dockerfile` serves as an excellent and reliable starting point.

**Conclusion**: This version is your "emergency" or "starter" kit. It is not for daily use but for solving
specific build problems or for a quick start.

---

## 2. Optimized Multi-Stage Dockerfile

This is a strategically important improvement for your project, which will be necessary when moving to CI/CD
and deploying to production servers.

```dockerfile
# 1. Install dependencies
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --only=production

# 2. Build the application
FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# 3. Final production image
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV production

RUN addgroup -g 1001 -S nodejs && adduser -S nextjs -u 1001

COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

USER nextjs
EXPOSE 3000
ENV PORT 3000
CMD ["npm", "start"]
```

### Advantages for CI/CD and Production

1.  **CI/CD Pipeline Optimization**:
    *   **Build Speed**: Docker caches the `deps` stage. Dependencies are reinstalled only when `package.json`
        changes, which significantly speeds up the CI/CD pipeline.
    *   **Reliability**: `npm ci` ensures that the same dependency versions are installed in CI/CD as locally,
        preventing "it works on my machine" errors.

2.  **Production Readiness (Deployment)**:
    *   **Small Image Size**: The final image (`runner`) contains only the compiled application and production
        dependencies, which speeds up its deployment to servers.
    *   **Security**: Running the application as a non-root user (`USER nextjs`) is a standard security practice
        to minimize risks.

**Conclusion**: This Dockerfile is the next step in the project's evolution. It should be implemented when you
start setting up CI/CD. You can use `Dockerfile.dev` for local development and `Dockerfile.prod` for production.