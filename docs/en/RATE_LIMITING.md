# Rate Limiting Implementation

## Overview
Phoebe CMS implements IP-based rate limiting using Bucket4j to prevent API abuse and ensure fair resource usage.

## Configuration

### Rate Limits
- **Public API** (`/api/public/**`): 100 requests per minute per IP
- **Admin API** (`/api/admin/**`): 50 requests per minute per IP (more restrictive)

### Implementation Details
- **Library**: Bucket4j 8.7.0
- **Strategy**: Token bucket algorithm with IP-based buckets
- **Storage**: In-memory ConcurrentHashMap (per application instance)
- **Refill**: Linear refill every minute

## Components

### RateLimitConfig
- Creates and manages buckets for different API types
- Separate bucket pools for public and admin APIs
- IP-based bucket identification with prefixes (`public:IP`, `admin:IP`)

### RateLimitFilter
- Servlet filter that intercepts all requests
- Extracts client IP from headers (X-Forwarded-For, X-Real-IP) or remote address
- Applies appropriate rate limit based on request path
- Returns HTTP 429 when limit exceeded

## Testing Rate Limits

### Test Public API (100 req/min):
```bash
# Single request
curl -i http://localhost:8080/api/public/news

# Rapid requests to trigger limit
for i in {1..105}; do 
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/public/news
done
```

### Test Admin API (50 req/min):
```bash
# With authentication
for i in {1..55}; do 
  curl -s -o /dev/null -w "%{http_code}\n" -u admin:password http://localhost:8080/api/admin/news
done
```

### Check Rate Limit Headers:
```bash
curl -i http://localhost:8080/api/public/news | grep "X-Rate-Limit"
```

## Response Headers
- `X-Rate-Limit-Remaining`: Number of requests remaining in current window

## Error Response
When rate limit is exceeded (HTTP 429):
```json
{
  "error": "Rate limit exceeded",
  "retryAfter": 60
}
```

## Production Considerations
- Current implementation uses in-memory storage (resets on restart)
- For distributed deployments, consider Redis-backed storage
- Monitor rate limit metrics for capacity planning
- Adjust limits based on actual usage patterns

## IP Address Detection
The filter checks headers in order:
1. `X-Forwarded-For` (proxy/load balancer)
2. `X-Real-IP` (reverse proxy)
3. `request.getRemoteAddr()` (direct connection)

This ensures proper rate limiting behind proxies and load balancers.