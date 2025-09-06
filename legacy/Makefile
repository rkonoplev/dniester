# ===========================================================================
# Makefile for Java Spring Boot News Platform
# ---------------------------------------------------------------------------
# Usage:
#   make test-news-get
#   make test-news-post
#   make test-news-post API_USER=youruser API_PASS=yourpass
#
# === Credentials ===
#   By default, placeholders are used.
#   NEVER commit real secrets to version control.
#
#   Override these via environment variables or directly on the command line.
#
# Example:
#   export API_USER=myuser
#   export API_PASS=mypass
#   make test-news-post
# ===========================================================================

API_USER ?= <YOUR_USERNAME>
API_PASS ?= <YOUR_PASSWORD>
API_URL  ?= http://localhost:8080

.PHONY: test-news-get test-news-post test-news-post-invalid

# Test GET /api/news endpoint
test-news-get:
	@echo "Requesting all news..."
	curl -u $(API_USER):$(API_PASS) -X GET $(API_URL)/api/news
	@echo

# Test POST /api/admin/news endpoint with valid data
# Usage: make test-news-post API_USER= API_PASS=
test-news-post:
	@echo "Posting new news item (valid data)..."
	curl -u $(API_USER):$(API_PASS) \
	  -H "Content-Type: application/json" \
	  -X POST $(API_URL)/api/admin/news \
	  -d '{"title":"Test title","content":"Test content","category":"general"}'
	@echo

# Test POST /api/admin/news endpoint with invalid data
# Usage: make test-news-post-invalid API_USER= API_PASS=
test-news-post-invalid:
	@echo "Posting new news item (invalid data)..."
	curl -u $(API_USER):$(API_PASS) \
	  -H "Content-Type: application/json" \
	  -X POST $(API_URL)/api/admin/news \
	  -d '{"title": "", "content": null}'
	@echo

# Delete news by ID
# Usage: make delete-news NEWS_ID=1
delete-news:
	@if [ -z "$(NEWS_ID)" ]; then \
		echo "Please specify NEWS_ID. Usage: make delete-news NEWS_ID=1"; \
	else \
		echo "Deleting news with id=$(NEWS_ID)..."; \
		curl -u $(API_USER):$(API_PASS) \
		  -X DELETE $(API_URL)/api/admin/news/$(NEWS_ID); \
		echo; \
	fi
