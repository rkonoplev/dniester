# Documentation
## API Testing with Makefile

This project includes a `Makefile` with handy commands to test API endpoints using `curl`.

**Usage:**

1. Copy or rename the file `Makefile` (with a capital "M") into your project root.
2. Set your API credentials as environment variables, or override them on the command line:

```bash
export API_USER=yourusername
export API_PASS=yourpassword
make test-news-post
```
or

```bash
make test-news-post API_USER=yourusername API_PASS=yourpassword
```

## Example API Requests (with curl)

Below are typical ways to interact with the News Platform API using `curl`.  
Replace `<USERNAME>` and `<PASSWORD>` with your account credentials.

---

### 1. Create a News Item

```bash
curl -u <USERNAME>:<PASSWORD> \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/api/admin/news \
  -d '{"title":"My news title","content":"Some content here","category":"general"}'
```

### 2. Delete a News Item by ID
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X DELETE http://localhost:8080/api/admin/news/<NEWS_ID>
   Replace <NEWS_ID> with the numeric ID of the news item you want to delete.
   ```

### 3. Get All News Items
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```

### 4. Check Number of News Records
   To check how many news records you currently have, you can run:
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```
### Note:

These examples use http://localhost:8080 for local development.
Replace with your API server address as needed.
Never use real passwords or production credentials in public documentation or scripts.