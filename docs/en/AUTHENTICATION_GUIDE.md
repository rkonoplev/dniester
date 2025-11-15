# Authentication Guide

This document explains the authentication architecture and security practices for Phoebe CMS.

---

## Authentication Architecture

### Current Implementation
- **Basic Authentication** using Spring Security.
- **User credentials are stored in the database** (hashed passwords). Environment variables are used to initialize these credentials.
- **Role-based access control** (RBAC).
- **BCrypt password encoding** with strength 12.
- **Rate limiting** integration.

### Security Principles
- **Separation of concerns**: User profiles and authentication credentials are distinct entities.
- **Environment configuration**: Environment variables are used to initialize user credentials in the database or for system accounts.
- **Secure credential storage**: User passwords are stored in the database only in hashed form (BCrypt). Plaintext passwords are never stored.
- **Role-based access**: ADMIN and EDITOR roles with different sets of permissions.

### CSRF Protection
- **CSRF (Cross-Site Request Forgery) protection** is disabled for the API. This is standard practice for RESTful APIs that typically use tokens (e.g., JWT) or other authentication mechanisms not susceptible to CSRF attacks.

---

## User Roles & Permissions

| Role | Permissions | Endpoints |
|---|---|---|
| ADMIN | Full system access, all content management. | `/api/admin/**` |
| EDITOR | Own content only (create, read, update). | `/api/admin/news/**` |

> **Note**: Detailed role implementation requirements are documented in
> [Role Security Implementation Guide](./SECURITY_ROLES.md).

---

## Configuration

### Environment Variables
```bash
# Admin credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=secureAdminPassword

# Editor credentials (optional)
EDITOR_USERNAME=editor
EDITOR_PASSWORD=secureEditorPassword
```

### Security Configuration
- **Password encoding**: BCrypt with strength 12.
- **Authentication**: HTTP Basic Auth.
- **Session management**: Stateless.
- **CSRF protection**: Disabled for API.

---

## Usage Examples

### Admin Access
```bash
# Get all news (admin endpoint)
curl -u admin:secureAdminPassword \
  http://localhost:8080/api/admin/news
```

### Editor Access
```bash
# Editor can view all news but only edit own content
curl -u editor:secureEditorPassword \
  -H "Content-Type: application/json" \
  -d '{"title":"My Article","content":"Content"}' \
  http://localhost:8080/api/admin/news
```

### Public Access
```bash
# No authentication required
curl http://localhost:8080/api/public/news?size=10
```

---

## Security Best Practices

### Development
- Use `.env` file for local credentials (never commit).
- Use strong, unique passwords for each environment.

### Production
- Set credentials via environment variables or secret files.
- Use container orchestration secrets management.
- Enable HTTPS/TLS for credential transmission.

### CI/CD
- Store credentials in GitHub Secrets.
- Never log or expose credentials in build outputs.

---

## Future Enhancements

- **OAuth 2.0 + 2FA migration**: Replace Basic Auth with OAuth 2.0 and two-factor authentication.
- **JWT tokens** for stateless authentication.
- **API key authentication** for service-to-service calls.
- **Audit logging** for authentication events.

---

## Security Considerations

### Current Limitations
- Basic Auth credentials transmitted with each request.
- No token expiration or refresh mechanism.
- **Limited audit trail for authentication events**: Currently, only failed login attempts are logged. Detailed logging of successful logins or other authentication events is not implemented.
- No account lockout or brute force protection.

### Mitigation Strategies
- **Rate limiting**: Prevents brute force attacks.
- **HTTPS enforcement**: Protects credential transmission.
- **Environment isolation**: Separate credentials per environment.
- **Monitoring**: Log authentication failures and suspicious activity.
