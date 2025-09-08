# 📡 API Usage Guide

This guide provides practical examples for testing the News Platform API endpoints using **curl**.

## 🚦 Rate Limiting

All API endpoints are rate limited by IP address:
- **Public API** (`/api/public/**`): 100 requests per minute
- **Admin API** (`/api/admin/**`): 50 requests per minute

Rate limit headers are included in responses:
- `X-Rate-Limit-Remaining`: Requests remaining in current window

When rate limit is exceeded, you'll receive HTTP 429:
```json
{"error":"Rate limit exceeded","retryAfter":60}
```

---
## 🌐 Public API Endpoints (No Authentication Required)

### 1. Get All Published News (with Pagination)
```bash
curl -i "http://localhost:8080/api/public/news?page=0&size=10&sort=publicationDate,desc"
```

### 2. Get Published News by ID
```bash
curl -i "http://localhost:8080/api/public/news/1"
```

### 3. Get Published News by Term ID (Category/Tag)
```bash
curl -i "http://localhost:8080/api/public/news/term/5?page=0&size=15"
```

### 4. Get Published News by Multiple Term IDs
```bash
curl -i "http://localhost:8080/api/public/news/terms?termIds=1,3,5&page=0&size=20"
```

### 5. Check Rate Limiting Headers
```bash
curl -i "http://localhost:8080/api/public/news" | grep "X-Rate-Limit"
```

---

## 🔒 Admin API Endpoints (Authentication Required)

### 1. Get All News (Published + Unpublished)
```bash
curl -u admin:password -i "http://localhost:8080/api/admin/news?page=0&size=10"
```

### 2. Create News Item
```bash
curl -u admin:password \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/api/admin/news \
  -d '{
    "title": "Breaking News Title",
    "body": "Full article content here...",
    "teaser": "Short summary for preview",
    "published": true,
    "publicationDate": "2024-01-15T10:30:00"
  }'
```

### 3. Update News Item
```bash
curl -u admin:password \
  -H "Content-Type: application/json" \
  -X PUT http://localhost:8080/api/admin/news/1 \
  -d '{
    "title": "Updated News Title",
    "body": "Updated content...",
    "published": false
  }'
```

### 4. Delete News Item
```bash
curl -u admin:password -X DELETE "http://localhost:8080/api/admin/news/1"
```
---

## 📋 Pagination Parameters

All list endpoints support pagination:
- `page`: Page number (0-based, default: 0)
- `size`: Items per page (default: 10, max: 100)
- `sort`: Sort field and direction (e.g., `publicationDate,desc`)

### Example with Pagination:
```bash
curl -i "http://localhost:8080/api/public/news?page=1&size=5&sort=title,asc"
```

### Response Format:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Sample News Title",
      "teaser": "Brief summary...",
      "publicationDate": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
```

---

## 📊 Rate Limiting Testing

### Test Public API Rate Limit (100/min):
```bash
for i in {1..105}; do 
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/public/news
done
```

### Test Admin API Rate Limit (50/min):
```bash
for i in {1..55}; do 
  curl -s -o /dev/null -w "%{http_code}\n" -u admin:password http://localhost:8080/api/admin/news
done
```

Expected: First requests return `200`, then `429` after limit exceeded.

---

## ⚠️ Notes
- All examples assume local development: `http://localhost:8080`
- Replace `admin:password` with your configured credentials
- Rate limits are per IP address and reset every minute
- Use `-i` flag to see response headers including rate limit info
- Never use real production passwords in scripts or documentation

## 📚 Alternative API Documentation: Swagger UI

In addition to this `API_USAGE.md`, you can access live API documentation and test endpoints in Swagger UI:

👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Swagger UI automatically generates API docs from controllers and annotations.
It is useful for exploring available endpoints, schemas, and making test requests directly in the browser.