# 📡 API Usage Guide

This guide provides practical examples for testing the News Platform API endpoints using **curl** and the **Makefile** helper.

---

## 🔧 Using the Makefile

The project provides a `Makefile` with handy targets for testing API endpoints.

### Setup your credentials
Either export them:
```bash
export API_USER=yourusername
export API_PASS=yourpassword
```
Or override them on the command line:

```bash
make test-news-post API_USER=yourusername API_PASS=yourpassword
```
### Example Makefile usage
```bash
make test-news-post
make test-news-delete API_USER=admin API_PASS=superpass
```
### 🌐 API Endpoints Examples with curl
#### 1. Create a News Item
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -H "Content-Type: application/json" \
   -X POST http://localhost:8080/api/admin/news \
   -d '{"title":"My news title","content":"Some content here","category":"general"}'
   ```
#### 2. Delete a News Item by ID
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X DELETE http://localhost:8080/api/admin/news/<NEWS_ID>
   Replace <NEWS_ID> with the numeric ID of the news record.
   ```
#### 3. Get All News Items
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```
#### 4. Check Number of News Records
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```
### ⚠️ Notes
   - All examples assume local development endpoint: http://localhost:8080.
   - Replace credentials (<USERNAME>/<PASSWORD>) with your configured admin user.
   - Never use real production passwords in scripts or documentation.
   - For automated QA, it is recommended to use seeded test accounts with limited privileges.
