# 🔐 Authentication Guide

This document explains the authentication architecture and security practices for the News Platform.

---

## 🏗️ Authentication Architecture

### Current Implementation
- **Basic Authentication** with Spring Security
- **Environment-based credentials** (no database storage)
- **Role-based access control** (RBAC)
- **BCrypt password encoding**
- **Rate limiting** integration

### Security Principles
- **Separation of concerns**: User profiles ≠ Authentication credentials
- **Environment configuration**: Credentials via ENV variables only
- **No credential storage**: Authentication data never persisted in database
- **Role-based access**: ADMIN and EDITOR roles with different permissions

---

## 👥 User Roles & Permissions

| Role    | Permissions                           | Endpoints                    |
|---------|---------------------------------------|------------------------------|
| ADMIN   | Full system access, user management   | `/api/admin/**`             |
| EDITOR  | Content management, limited access    | `/api/admin/news/**`        |
| USER    | Read-only access to published content | `/api/public/**`            |

**Note**: PUBLIC role will be replaced with USER role in future OAuth 2.0 + 2FA migration.

---

## 🔧 Configuration

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
- **Password encoding**: BCrypt with strength 12
- **Authentication**: HTTP Basic Auth
- **Session management**: Stateless (no sessions)
- **CSRF protection**: Disabled for API-only usage

---

## 🚀 Usage Examples

### Admin Access
```bash
# Get all news (admin endpoint)
curl -u admin:secureAdminPassword \
  http://localhost:8080/api/admin/news

# Create new article
curl -u admin:secureAdminPassword \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","content":"Content","authorId":1}' \
  http://localhost:8080/api/admin/news
```

### Editor Access
```bash
# Editor can manage content
curl -u editor:secureEditorPassword \
  http://localhost:8080/api/admin/news

# But cannot access user management
curl -u editor:secureEditorPassword \
  http://localhost:8080/api/admin/users
# Returns 403 Forbidden
```

### Public Access
```bash
# No authentication required
curl http://localhost:8080/api/public/news?size=10
```

---

## 🛡️ Security Best Practices

### Development
- Use `.env` file for local credentials (never commit)
- Use strong, unique passwords for each environment
- Rotate credentials regularly

### Production
- Set credentials via environment variables or secret files
- Use container orchestration secrets management
- Enable HTTPS/TLS for credential transmission
- Monitor authentication attempts and failures

### CI/CD
- Store credentials in GitHub Secrets
- Use separate credentials for each environment
- Never log or expose credentials in build outputs

---

## 🔄 Future Enhancements

### Planned Features
- **OAuth 2.0 + 2FA migration**: Replace Basic Auth with OAuth 2.0 and two-factor authentication for all roles (ADMIN, EDITOR, USER)
- **JWT tokens** for stateless authentication
- **Two-factor authentication** (2FA) for enhanced security
- **API key authentication** for service-to-service calls
- **Audit logging** for authentication events

### Database Integration
- User profile management (separate from authentication)
- Role assignment and permissions
- User activity tracking
- Password reset functionality (when JWT implemented)

---

## ⚠️ Security Considerations

### Current Limitations
- Basic Auth credentials transmitted with each request
- No token expiration or refresh mechanism
- Limited audit trail for authentication events
- No account lockout or brute force protection

### Mitigation Strategies
- **Rate limiting**: Prevents brute force attacks
- **HTTPS enforcement**: Protects credential transmission
- **Environment isolation**: Separate credentials per environment
- **Monitoring**: Log authentication failures and suspicious activity

---

## 📚 Related Documentation

- [Configuration Guide](CONFIG_GUIDE.md) - Environment and profile setup
- [CI/CD & Security](CI_CD_SECURITY.md) - Pipeline security practices
- [API Usage Guide](API_USAGE.md) - Endpoint authentication requirements
- [Admin Panel Specification](ADMIN_PANEL_SPEC.md) - Role-based UI requirements