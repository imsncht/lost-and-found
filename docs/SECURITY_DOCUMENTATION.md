# Security Documentation - Lost & Found Project

## 1. Authentication & Authorization

### 1.1 Authentication Mechanism

**Implementation**: Session-based authentication with password hashing

**Flow**:
1. User submits email & password via login form
2. `UserService.login()` retrieves user from database by email
3. Password verified using BCrypt: `PasswordUtil.checkPassword(plainPassword, hashedPassword)`
4. On success, `User` object stored in HTTP session: `session.setAttribute("user", user)`
5. Session validated on every request via `AuthFilter`

**Code Example**:
```java
// UserService.java
public User login(String email, String password) {
    User user = userDAO.findByEmail(email);
    
    if (user == null) {
        return null;  // User not found
    }
    
    // Verify password using BCrypt
    if (PasswordUtil.checkPassword(password, user.getPassword())) {
        return user;  // Authentication successful
    }
    
    return null;  // Password mismatch
}
```

### 1.2 Password Security

**Algorithm**: BCrypt (industry-standard hashing)
- **Library**: jbcrypt (org.mindrot.jbcrypt)
- **Salt**: Automatically generated per password
- **Work Factor**: Default bcrypt cost (10+ rounds)
- **Never**: Store plaintext passwords

**Implementation**:
```java
// PasswordUtil.java
public static String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
    // Generates: $2a$10$[22 chars salt][31 chars hash]
}

public static boolean checkPassword(String plainPassword, String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
}
```

**Password Requirements** (Not enforced currently):
- Recommended minimum 8 characters
- Mix of uppercase, lowercase, numbers
- Consider adding password strength validation in future versions

### 1.3 Session Management

**Session Configuration**:
- **Type**: HTTP Session (javax.servlet.http.HttpSession)
- **Timeout**: Default Tomcat timeout (usually 30 minutes)
- **Storage**: Server-side in memory
- **Creation**: `req.getSession()` on successful login
- **Invalidation**: `req.getSession().invalidate()` on logout

**Security Considerations**:
```java
// ✓ Good: Create session on success
HttpSession session = req.getSession();
session.setAttribute("user", user);

// ✓ Good: Invalidate on logout
public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    if ("/logout".equals(path)) {
        req.getSession().invalidate();
        resp.sendRedirect("login");
    }
}

// ✗ Bad: Using session before login
HttpSession session = req.getSession();  // Creates session immediately
// Session exists even if user not authenticated
```

---

## 2. Authorization

### 2.1 Role-Based Access Control (RBAC)

**Roles**:
- **USER**: Regular users (default role for new registrations)
- **ADMIN**: Administrative users (hardcoded default admin)

**Role Checking**:
```java
// User.java
public boolean isAdmin() {
    return "ADMIN".equals(role);
}

// AuthFilter.java
User user = (User) session.getAttribute("user");

if (path.startsWith("/admin") && !user.isAdmin()) {
    resp.sendRedirect(req.getContextPath() + "/items");
    return;
}
```

### 2.2 Access Control Lists (ACL)

**Protected Routes**:

| Route | Required Role | Status |
|-------|--------------|--------|
| `/login` | None | Public ✓ |
| `/register` | None | Public ✓ |
| `/items` | USER | Protected ✓ |
| `/items/post` | USER | Protected ✓ |
| `/claims/submit` | USER | Protected ✓ |
| `/admin/*` | ADMIN | Protected ✓ |

**Implementation via AuthFilter**:
```java
@WebFilter("/*")
public class AuthFilter implements Filter {
    
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();
        
        // ✓ Public pages - no authentication required
        if (path.startsWith("/css/") || path.equals("/login") 
            || path.equals("/register")) {
            chain.doFilter(request, response);
            return;
        }
        
        // ✓ Check session exists
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // ✓ Check admin role for admin pages
        User user = (User) session.getAttribute("user");
        if (path.startsWith("/admin") && !user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/items");
            return;
        }
        
        chain.doFilter(request, response);
    }
}
```

---

## 3. Input Validation & Injection Prevention

### 3.1 SQL Injection Prevention

**Current Protection**: Using Hibernate ORM with parameterized queries

**Safe Implementation**:
```java
// ✓ Good: Parameterized query using HQL
Query<Item> query = session.createQuery(
    "from Item where title like :k or category like :k or location like :k",
    Item.class
);
query.setParameter("k", "%" + keyword + "%");
List<Item> list = query.list();

// ✗ Bad: String concatenation (vulnerable)
String hql = "from Item where title like '%" + keyword + "%'";  // SQL injection risk
```

**Best Practices**:
1. **Always use parameterized queries**
2. **Never concatenate user input into SQL strings**
3. **Use Hibernate's type-safe query API**

### 3.2 Cross-Site Scripting (XSS) Prevention

**Current Implementation**: JSP's EL (Expression Language) auto-escapes HTML

**Safe Output**:
```jsp
<!-- ✓ Good: Auto-escaped -->
<p>${item.title}</p>

<!-- ✗ Bad: Raw HTML (XSS risk) -->
<%= item.getDescription() %>
```

**Potential Vulnerability**:
```java
// Risk: User can input HTML/JavaScript in description
item.setDescription("<img src=x onerror='alert(1)'>");
```

**Recommendations for Future**:
- Add server-side HTML sanitization library (e.g., OWASP HTML Sanitizer)
- Validate input length and character restrictions
- Encode output consistently

### 3.3 Input Length Restrictions

**Database Level** (schema.sql):
```sql
CREATE TABLE items (
    title VARCHAR(150) NOT NULL,           -- Max 150 chars
    description TEXT,                      -- Large text
    category VARCHAR(80),                  -- Max 80 chars
    location VARCHAR(150),                 -- Max 150 chars
    message TEXT (in claims)               -- Large text
);
```

**Application Level** (potential improvements):
```java
// ✓ Future: Validate input lengths
public void postItem(String title, String description, ...) {
    if (title == null || title.length() > 150) {
        throw new IllegalArgumentException("Invalid title length");
    }
    if (description.length() > 3000) {
        throw new IllegalArgumentException("Description too long");
    }
}
```

---

## 4. File Upload Security

### 4.1 File Upload Implementation

**Current Security**:
```java
// ItemServlet.java - File upload handling
Part filePart = req.getPart("image");

String fileName = System.currentTimeMillis() + "_" + 
                  filePart.getSubmittedFileName();

// ✓ Good: Timestamp prevents name collisions
// ✓ Good: Stored outside web root (user home directory)
String uploadPath = System.getProperty("user.home") + File.separator 
                  + "lostfound" + File.separator + "uploads";

if (filePart.getSize() > 0) {
    filePart.write(uploadPath + File.separator + fileName);
}
```

### 4.2 File Upload Vulnerabilities & Mitigations

**Potential Risks**:
1. **Path Traversal**: User uploads `../../../etc/passwd`
2. **Large Files**: Denial of Service (DoS) by uploading huge files
3. **Malicious Files**: Uploading executables or malware

**Current Protections**:
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB threshold
    maxFileSize = 5 * 1024 * 1024,        // 5 MB max per file
    maxRequestSize = 10 * 1024 * 1024     // 10 MB max per request
)
```

**Recommendations for Improvement**:
```java
// ✓ Future: Validate file type
String mimeType = filePart.getContentType();
if (!mimeType.startsWith("image/")) {
    throw new IllegalArgumentException("Only images allowed");
}

// ✓ Future: Sanitize filename
String sanitized = fileName.replaceAll("[^a-zA-Z0-9_.-]", "");

// ✓ Future: Validate image dimensions/content
BufferedImage img = ImageIO.read(filePart.getInputStream());
if (img == null) {
    throw new IllegalArgumentException("Invalid image file");
}

// ✓ Future: Store outside web directory (already done)
// ✓ Future: Validate path doesn't contain traversal
if (uploadPath.contains("..") || uploadPath.contains("~")) {
    throw new SecurityException("Invalid path");
}
```

---

## 5. CSRF & Session Security

### 5.1 CSRF (Cross-Site Request Forgery) Protection

**Current Status**: Not explicitly implemented

**Risk Example**:
```html
<!-- Malicious site -->
<img src="https://lostfound.com/admin/approve?id=123" />
<!-- Admin viewing this would auto-approve claim -->
```

**Recommendations**:
1. **Add CSRF tokens** to all form submissions
2. **Use SameSite cookie attribute**
3. **Verify Referer header**

**Future Implementation Example**:
```java
// Generate token
String csrfToken = UUID.randomUUID().toString();
session.setAttribute("csrfToken", csrfToken);

// Verify token
String submittedToken = req.getParameter("_csrf");
String sessionToken = (String) req.getSession().getAttribute("csrfToken");

if (submittedToken == null || !submittedToken.equals(sessionToken)) {
    resp.sendError(403, "Invalid CSRF token");
    return;
}
```

### 5.2 Session Fixation Prevention

**Mitigation**: Create new session after successful authentication
```java
// Current implementation
HttpSession session = req.getSession();  // Creates new if needed
session.setAttribute("user", user);
```

**Better Implementation**:
```java
// Invalidate old session and create new one
req.getSession().invalidate();
HttpSession newSession = req.getSession(true);
newSession.setAttribute("user", user);
```

---

## 6. Sensitive Data Handling

### 6.1 Passwords

**Storage**:
- ✓ **Good**: Hashed with BCrypt
- **Not stored in**: Logs, error messages, code comments

**Transmission**:
- ✓ **Use HTTPS**: Encrypt in transit (recommended for production)
- ✗ **Avoid HTTP**: Passwords transmitted in plaintext

### 6.2 Personal Information

**Data Stored**:
- User email (unique identifier)
- User name
- Item descriptions
- Claim details
- Identifying marks

**Recommendations**:
1. Limit data collection to necessary fields only
2. Add user consent for data collection
3. Implement data retention policy
4. Add data deletion capability for users

### 6.3 Logging Best Practices

**Current Implementation**:
```java
// ✗ Risk: Logging passwords
System.out.println("User login attempt: " + email + " / " + password);

// ✓ Safe: Log only email, not password
System.out.println("User login attempt: " + email);

// ✓ Safe: Log success/failure without details
System.out.println("Login FAILED for user: " + email);
```

**Recommendations**:
```java
// Use proper logging framework (SLF4J/Logback)
import org.slf4j.Logger;

logger.info("User registered: {}", email);
logger.warn("Failed login attempt for: {}", email);
logger.error("Database error while processing claim: {}", id);

// ✗ Never log
logger.debug("Password: " + password);
logger.info("Claim verification details: " + verificationCode);
```

---

## 7. Security Checklist

### Authentication
- [x] Passwords hashed with BCrypt
- [x] Session-based authentication implemented
- [x] Default admin account hardcoded (should be configuration)
- [ ] Password strength validation
- [ ] Password expiry policy
- [ ] Account lockout after failed attempts
- [ ] Two-factor authentication (2FA)

### Authorization
- [x] Role-based access control (RBAC)
- [x] AuthFilter prevents unauthorized access
- [ ] Fine-grained permissions (beyond roles)
- [ ] Audit logging of admin actions

### Input Validation
- [x] SQL injection prevented via parameterized queries
- [x] XSS prevention via JSP auto-escaping
- [ ] Comprehensive input validation framework
- [ ] File upload type/size validation
- [ ] HTML sanitization for user content

### Session Security
- [x] Session created after login
- [x] Session invalidated on logout
- [ ] Session fixation prevention
- [ ] CSRF token protection
- [ ] Secure cookie settings (HttpOnly, Secure, SameSite)

### Data Security
- [x] Sensitive data (passwords) hashed
- [ ] Data encryption at rest
- [ ] HTTPS/TLS for data in transit
- [ ] Data retention policy
- [ ] User data deletion capability

### Error Handling
- [x] Generic error messages to users
- [ ] Detailed error logging for developers
- [ ] No stack traces in user responses
- [ ] Graceful error recovery

---

## 8. Production Deployment Security

### 8.1 Configuration

Before deploying to production:

1. **Update Default Admin Credentials**
   ```java
   // Move to environment variables
   ADMIN_EMAIL=secure@company.com
   ADMIN_PASSWORD=strongPassword
   ```

2. **Configure HTTPS**
   ```xml
   <!-- Enable SSL in Tomcat -->
   <Connector port="8443" protocol="HTTP/1.1"
              SSLEnabled="true" scheme="https" secure="true"
              keystoreFile="path/to/keystore" keystorePass="..."/>
   ```

3. **Update Hibernate Configuration**
   ```xml
   <!-- Use environment variables, not hardcoded credentials -->
   <property name="hibernate.connection.url">
       jdbc:mysql://db-server:3306/lostfound
   </property>
   <property name="hibernate.connection.username">${DB_USER}</property>
   <property name="hibernate.connection.password">${DB_PASS}</property>
   ```

4. **Database Security**
   - Create limited database user (not root)
   - Restrict database access to app server only
   - Use strong passwords
   - Regular backups

5. **Web Server Security**
   - Run Tomcat as non-root user
   - Set restrictive file permissions
   - Disable unnecessary services
   - Keep dependencies updated

### 8.2 Security Headers

**Recommended HTTP Headers**:
```xml
<!-- Add to web.xml or filter -->
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

---

## 9. Dependencies Security

### Current Dependencies
```xml
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.15.Final</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Recommendations**:
1. Regularly check for CVE (Common Vulnerabilities and Exposures)
2. Update dependencies to latest secure versions
3. Use OWASP Dependency-Check tool
4. Monitor security advisories

---

## 10. Incident Response

### 10.1 Security Breach Response Plan

1. **Detection**: Monitor logs for suspicious activity
2. **Isolation**: Take affected system offline if needed
3. **Investigation**: Determine scope and impact
4. **Notification**: Inform affected users
5. **Remediation**: Fix vulnerability
6. **Recovery**: Restore from backups if needed
7. **Review**: Implement preventive measures

### 10.2 Regular Security Audits

- [ ] Monthly log review
- [ ] Quarterly penetration testing
- [ ] Annual security assessment
- [ ] Dependency vulnerability scanning

---

## 11. References & Resources

- **OWASP Top 10**: https://owasp.org/www-project-top-ten/
- **Spring Security Guide**: https://spring.io/projects/spring-security
- **Hibernate Security**: https://hibernate.org/orm/security/
- **BCrypt Guide**: https://www.mindrot.org/projects/jbcrypt/
- **NIST Cybersecurity**: https://www.nist.gov/

---

**Last Updated**: April 22, 2026  
**Version**: 1.0  
**Reviewed by**: Development Team
