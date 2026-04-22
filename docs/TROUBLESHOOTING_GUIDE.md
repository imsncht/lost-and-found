# Troubleshooting Guide - Lost & Found Project

## Table of Contents
1. [Setup & Installation Issues](#setup--installation-issues)
2. [Database Problems](#database-problems)
3. [Authentication & Authorization Errors](#authentication--authorization-errors)
4. [File Upload Issues](#file-upload-issues)
5. [Runtime Errors](#runtime-errors)
6. [Servlet & JSP Problems](#servlet--jsp-problems)
7. [Performance Issues](#performance-issues)
8. [Deployment Issues](#deployment-issues)
9. [Common Bugs & Fixes](#common-bugs--fixes)

---

## Setup & Installation Issues

### Problem 1.1: Maven Build Fails

**Error**:
```
[ERROR] COMPILATION ERROR
[ERROR] /path/to/project/src/main/java/com/geca/lostfound/dao/UserDAO.java
```

**Possible Causes**:
- Java version mismatch
- Dependencies not downloaded
- Missing maven installation

**Solutions**:

**Step 1: Check Java version**
```bash
java -version
# Expected: Java 8 or higher (project uses Java 8)
```

**Step 2: Update pom.xml if needed**
```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**Step 3: Clean and rebuild**
```bash
mvn clean
mvn install -U  # -U forces dependency update
```

**Step 4: If still failing, check specific error**
```bash
mvn compile -X  # Enable debug output
```

---

### Problem 1.2: Dependency Download Fails

**Error**:
```
[ERROR] Failed to execute goal on project lost-and-found: 
Could not resolve dependencies for project
```

**Possible Causes**:
- Network connectivity issue
- Maven repository down
- Invalid pom.xml

**Solutions**:

**Step 1: Check internet connection**
```bash
ping repository.maven.apache.org
```

**Step 2: Update Maven settings**
```bash
# Edit ~/.m2/settings.xml
# Add alternate repository if needed
```

**Step 3: Delete local cache and retry**
```bash
rm -rf ~/.m2/repository
mvn clean install
```

**Step 4: Check dependency versions**
```bash
mvn dependency:tree  # View dependency tree
```

---

### Problem 1.3: IDE Cannot Find Classes

**Error**:
```
Cannot resolve symbol 'UserDAO'
Package not recognized
```

**Possible Causes**:
- IDE not configured properly
- Build paths incorrect
- Cache issues

**Solutions**:

**For Eclipse**:
1. Right-click project → Properties
2. Java Build Path → Source → Add folder `src/main/java`
3. Libraries → Add external JARs from `target/` folder
4. Project → Clean

**For IntelliJ IDEA**:
1. File → Project Structure
2. Modules → Sources → Mark `src/main/java` as Sources
3. File → Invalidate Caches → Restart IDE

**For VS Code**:
1. Install "Extension Pack for Java"
2. Command Palette → Java: Clean Language Server Workspace
3. Reload window

---

## Database Problems

### Problem 2.1: Cannot Connect to MySQL

**Error**:
```
Hibernate Error: org.hibernate.HibernateException
Unable to instantiate default tuplizer
com.mysql.cj.jdbc.exceptions.CommunicationsException:
Communications link failure
```

**Possible Causes**:
- MySQL server not running
- Wrong credentials
- Database not created
- Connection URL incorrect

**Solutions**:

**Step 1: Check MySQL is running**
```bash
# Windows
sc query MySQL80
# or
netstat -ano | findstr :3306

# Linux/Mac
ps aux | grep mysql
sudo systemctl status mysql
```

**Start MySQL if not running**:
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
```

**Step 2: Verify credentials in hibernate.cfg.xml**
```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/lostfound
</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">root@password</property>
```

**Step 3: Check if database exists**
```sql
-- Log into MySQL
mysql -u root -p

-- List databases
SHOW DATABASES;

-- Should show: lostfound database
```

**Step 4: Create database if missing**
```sql
CREATE DATABASE IF NOT EXISTS lostfound;
USE lostfound;

-- Import schema
SOURCE path/to/sql/schema.sql;

-- Verify tables created
SHOW TABLES;
```

**Step 5: Test connection using MySQL CLI**
```bash
mysql -h localhost -u root -proot@password lostfound
# If successful, you'll see mysql> prompt
```

---

### Problem 2.2: "Unknown Database 'lostfound'"

**Error**:
```
com.mysql.cj.jdbc.exceptions.MySQLSyntaxErrorException:
Unknown database 'lostfound'
```

**Solutions**:

```sql
-- Step 1: Log in to MySQL
mysql -u root -p

-- Step 2: Create database
CREATE DATABASE lostfound;
USE lostfound;

-- Step 3: Run schema
SOURCE /path/to/sql/schema.sql;

-- Step 4: Verify
SELECT COUNT(*) FROM users;  -- Should return 1 (admin user)
SELECT COUNT(*) FROM items;  -- Should return 0 initially
SELECT COUNT(*) FROM claims; -- Should return 0 initially
```

---

### Problem 2.3: "Access Denied for user 'root'@'localhost'"

**Error**:
```
com.mysql.cj.jdbc.exceptions.MySQLNonTransientConnectionException:
Access denied for user 'root'@'localhost' (using password: YES)
```

**Possible Causes**:
- Wrong password in hibernate.cfg.xml
- MySQL user/password not set correctly
- Special characters in password not escaped

**Solutions**:

**Step 1: Test MySQL connection directly**
```bash
mysql -u root -proot@password -h localhost

# If it fails, password is wrong
# If @ symbol in password, escape it:
mysql -u root -p'your@password' -h localhost
```

**Step 2: Reset MySQL root password (if forgotten)**

**Windows**:
```bash
# Stop MySQL
net stop MySQL80

# Start in safe mode
mysqld --skip-grant-tables

# In another terminal:
mysql -u root
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
```

**Linux/Mac**:
```bash
sudo systemctl stop mysql
sudo mysqld_safe --skip-grant-tables &

mysql -u root
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';

sudo systemctl restart mysql
```

**Step 3: Update hibernate.cfg.xml with correct password**
```xml
<property name="hibernate.connection.password">
    root@password
</property>
```

---

### Problem 2.4: "Table 'lostfound.users' doesn't exist"

**Error**:
```
com.mysql.cj.jdbc.exceptions.MySQLSyntaxErrorException:
Table 'lostfound.users' doesn't exist
```

**Solutions**:

```sql
-- Check if tables exist
USE lostfound;
SHOW TABLES;

-- If empty, run schema.sql
SOURCE /path/to/sql/schema.sql;

-- Verify tables
DESCRIBE users;
DESCRIBE items;
DESCRIBE claims;

-- Verify admin user
SELECT * FROM users;
```

---

### Problem 2.5: Hibernate Session Issues

**Error**:
```
HibernateException: Could not obtain connection from environment
LazyInitializationException: could not initialize proxy
```

**Solutions**:

**Issue**: Session closed but lazy-loaded properties accessed

```java
// ✗ Bad: Session closed, trying to access lazy property
User user = userDAO.findById(1);
userDAO.closeSession();  // or manually close
String name = user.getName();  // LazyInitializationException

// ✓ Good: Access properties before closing session
User user = userDAO.findById(1);
String name = user.getName();  // Access before close
userDAO.closeSession();
```

**Always ensure session stays open** while accessing entity properties:
```java
public User findById(Long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    User user = session.get(User.class, id);
    
    // Access properties while session open
    String name = user.getName();
    
    session.close();
    return user;  // User object still valid, but properties accessed
}
```

---

## Authentication & Authorization Errors

### Problem 3.1: Stuck on Login Page

**Error**: Cannot login even with correct credentials

**Possible Causes**:
- Password hashing issue
- Database default admin not created
- Session not being set

**Solutions**:

**Step 1: Verify admin user in database**
```sql
USE lostfound;
SELECT * FROM users WHERE role='ADMIN';

-- Should show:
-- id | name | email | password | role
-- 1  | Admin | admin@lostfound.com | $2a$10$... | ADMIN
```

**Step 2: If no admin, insert default admin**
```sql
INSERT INTO users(name, email, password, role) VALUES(
    'Admin',
    'admin@lostfound.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiM8zY6P6KycdxY6SY3Y0JVpaz6RtZ2',
    'ADMIN'
);
```

**This hash is for password: `password`**

**Step 3: Test login with credentials**
```
Email: admin@lostfound.com
Password: password
```

**Step 4: Check if UserDAO is querying correctly**
```java
// In UserService.java, add debug output
public User login(String email, String password) {
    User user = userDAO.findByEmail(email);
    
    if (user == null) {
        System.out.println("User not found: " + email);
        return null;
    }
    
    System.out.println("User found: " + user.getName());
    
    boolean matches = PasswordUtil.checkPassword(password, user.getPassword());
    System.out.println("Password matches: " + matches);
    
    return matches ? user : null;
}
```

**Step 5: Regenerate admin password hash**
```java
// Run this to generate new hash
String hashed = PasswordUtil.hashPassword("password");
System.out.println("Hashed: " + hashed);

// Update database:
// UPDATE users SET password='<new_hash>' WHERE email='admin@lostfound.com';
```

---

### Problem 3.2: "Unauthorized Access" or Redirected to Login

**Error**: Keep getting redirected to login even after logging in

**Possible Causes**:
- Session not persisting
- AuthFilter rejecting valid sessions
- Browser cookies disabled

**Solutions**:

**Step 1: Enable browser cookies**
- Chrome: Settings → Privacy and Security → Cookies → Allow all cookies
- Firefox: Preferences → Privacy → Cookies → Allow

**Step 2: Check Session is being created**
```java
// In AuthServlet.doPost()
HttpSession session = req.getSession();
User user = userService.login(email, password);

if (user != null) {
    session.setAttribute("user", user);
    System.out.println("Session ID: " + session.getId());
    System.out.println("User set: " + user.getEmail());
}
```

**Step 3: Check AuthFilter is reading session correctly**
```java
// In AuthFilter.doFilter()
HttpSession session = req.getSession(false);
if (session != null) {
    User user = (User) session.getAttribute("user");
    System.out.println("Session found: " + session.getId());
    System.out.println("User in session: " + (user != null ? user.getEmail() : "null"));
} else {
    System.out.println("No session found");
}
```

**Step 4: Clear browser cache/cookies**
```bash
# Chrome: Ctrl+Shift+Delete (Windows) or Cmd+Shift+Delete (Mac)
# Firefox: Ctrl+Shift+Delete

# Or manually:
# Settings → Clear browsing data → Cookies and cached images
```

**Step 5: Check session timeout**
```bash
# In Tomcat, default is 30 minutes
# In context.xml or web.xml:
<session-config>
    <cookie-http-only>true</cookie-http-only>
    <tracking-mode>COOKIE</tracking-mode>
</session-config>
```

---

### Problem 3.3: Admin Cannot Access Admin Pages

**Error**: `Error 403` or redirected back to `/items`

**Possible Causes**:
- User not marked as ADMIN in database
- AuthFilter not checking role correctly

**Solutions**:

**Step 1: Verify user is ADMIN in database**
```sql
SELECT id, email, role FROM users;

-- Should show role='ADMIN'
```

**Step 2: If not ADMIN, update user**
```sql
UPDATE users SET role='ADMIN' WHERE email='admin@lostfound.com';
```

**Step 3: Clear session and re-login**
- Logout
- Clear browser cookies
- Login again

**Step 4: Verify AuthFilter logic**
```java
// In AuthFilter.java
if (path.startsWith("/admin") && !user.isAdmin()) {
    System.out.println("Access denied - user not admin: " + user.getEmail());
    resp.sendRedirect(req.getContextPath() + "/items");
    return;
}
```

---

## File Upload Issues

### Problem 4.1: Images Not Displaying

**Error**: Image showing as broken (404) or not loading

**Possible Causes**:
- File not uploaded to filesystem
- Image path not stored in database
- ImageServlet cannot find file

**Solutions**:

**Step 1: Verify upload directory exists**
```bash
# Windows
cd %USERPROFILE%\lostfound\uploads
dir

# Linux/Mac
cd ~/lostfound/uploads
ls -la
```

**Step 2: If directory missing, create it**
```bash
# Windows
mkdir %USERPROFILE%\lostfound\uploads

# Linux/Mac
mkdir -p ~/lostfound/uploads
chmod 755 ~/lostfound/uploads
```

**Step 3: Check file permissions**
```bash
# Linux/Mac - ensure readable
chmod 644 ~/lostfound/uploads/*
```

**Step 4: Verify database stores image path**
```sql
SELECT id, title, imagePath FROM items;

-- imagePath should show: 1713712345678_photo.jpg
-- (timestamp_originalFilename)
```

**Step 5: Check ImageServlet is receiving correct filename**
```java
// In ImageServlet.java
String fileName = req.getParameter("name");
System.out.println("Requested file: " + fileName);

String path = System.getProperty("user.home") + File.separator 
            + "lostfound" + File.separator + "uploads" 
            + File.separator + fileName;
System.out.println("Full path: " + path);

File file = new File(path);
System.out.println("File exists: " + file.exists());
```

**Step 6: Test URL directly**
```
http://localhost:8080/lost-and-found/images?name=1713712345678_photo.jpg

# Check console output to verify path
```

---

### Problem 4.2: Upload Fails with "File Size Exceeded"

**Error**:
```
413 Payload Too Large
The request entity is larger than the server is willing to process
```

**Solutions**:

**Step 1: Check current limits in ItemServlet**
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 5 * 1024 * 1024,        // 5 MB per file
    maxRequestSize = 10 * 1024 * 1024     // 10 MB per request
)
```

**Step 2: If you need larger files, increase limits**
```java
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,      // 2 MB
    maxFileSize = 20 * 1024 * 1024,           // 20 MB per file
    maxRequestSize = 50 * 1024 * 1024         // 50 MB per request
)
```

**Step 3: Also update Tomcat configuration**
```xml
<!-- In CATALINA_BASE/conf/context.xml -->
<Context maxPostSize="52428800">  <!-- 50 MB -->
```

**Step 4: Restart Tomcat and retry upload**

---

### Problem 4.3: "Bad Filename" Error

**Error**: Upload fails when filename contains special characters

**Solutions**:

**Step 1: Understanding the issue**
```java
// Current implementation uses timestamp + original filename
String fileName = System.currentTimeMillis() + "_" + 
                  filePart.getSubmittedFileName();

// Problem: User uploads "my file (1).jpg"
// Result: "1713712345678_my file (1).jpg"
// Parentheses might cause issues
```

**Step 2: Sanitize filename**
```java
// ✓ Better implementation
String originalName = filePart.getSubmittedFileName();
String sanitized = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
String fileName = System.currentTimeMillis() + "_" + sanitized;

// Example: "my file (1).jpg" → "my_file_1_.jpg"
```

**Step 3: Apply fix to ItemServlet**
```java
// In ItemServlet.doPost()
String originalFileName = filePart.getSubmittedFileName();
String sanitizedName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
String fileName = System.currentTimeMillis() + "_" + sanitizedName;
```

---

## Runtime Errors

### Problem 5.1: NullPointerException

**Error**:
```
Exception: java.lang.NullPointerException
at com.geca.lostfound.service.ItemService.postItem(ItemService.java:25)
```

**Possible Causes**:
- Null object accessed without checking
- Null returned from DAO
- Missing session attribute

**Solutions**:

**Step 1: Check the line causing error**
```java
// Line 25 in ItemService.java
public void postItem(String title, String description, ..., User user) {
    Item item = new Item();
    item.setTitle(title);  // ← NullPointerException if title is null
}
```

**Step 2: Add null checks**
```java
// ✓ Good: Validate input
public void postItem(String title, String description, ..., User user) {
    if (title == null || title.trim().isEmpty()) {
        throw new IllegalArgumentException("Title cannot be empty");
    }
    if (user == null) {
        throw new IllegalArgumentException("User must be authenticated");
    }
    // ... continue
}
```

**Step 3: Check DAO returns null**
```java
// ✓ Good: Check null from DAO
Item item = itemDAO.findById(itemId);
if (item == null) {
    System.out.println("Item not found: " + itemId);
    return;  // Early exit
}
```

**Step 4: Add logging to track null values**
```java
System.out.println("Item ID: " + itemId);
Item item = itemDAO.findById(itemId);
System.out.println("Item found: " + (item != null ? item.getId() : "null"));
```

---

### Problem 5.2: ClassNotFoundException

**Error**:
```
ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Possible Causes**:
- MySQL JDBC driver not in classpath
- Maven dependency not downloaded

**Solutions**:

**Step 1: Verify dependency in pom.xml**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Step 2: Check JAR is in target**
```bash
ls -la target/WEB-INF/lib/ | grep mysql
```

**Step 3: If missing, rebuild**
```bash
mvn clean install
mvn tomcat:deploy
```

---

### Problem 5.3: "Cannot instantiate connection pool"

**Error**:
```
Hibernate Error: Cannot instantiate connection pool class
```

**Possible Causes**:
- hibernate.cfg.xml malformed
- Missing MySQL driver property

**Solutions**:

**Step 1: Verify hibernate.cfg.xml syntax**
```xml
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- Check all properties are properly closed -->
        <property name="hibernate.connection.driver_class">
            com.mysql.cj.jdbc.Driver
        </property>
        
        <!-- No errors above? -->
    </session-factory>
</hibernate-configuration>
```

**Step 2: Test with simple connection**
```java
// In a test class
public static void main(String[] args) {
    try {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        System.out.println("Connected: " + (sf != null));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
}
```

---

## Servlet & JSP Problems

### Problem 6.1: 404 Page Not Found

**Error**:
```
HTTP 404 - /lost-and-found/items (Not Found)
```

**Possible Causes**:
- Servlet not mapped correctly
- Wrong URL path
- Web application not deployed

**Solutions**:

**Step 1: Check servlet mapping**
```java
// In ItemServlet.java
@WebServlet(urlPatterns = {"/items", "/items/post", "/items/detail", 
                           "/items/delete", "/items/close", "/items/archive"})
public class ItemServlet extends HttpServlet {
    // ...
}
```

**Step 2: Verify correct URL in browser**
```
✓ Correct: http://localhost:8080/lost-and-found/items
✗ Wrong: http://localhost:8080/items
✗ Wrong: http://localhost:8080/lost-and-found/item
```

**Step 3: Check context path in pom.xml**
```xml
<properties>
    <!-- If not set, uses project artifactId: lost-and-found -->
</properties>
```

**Step 4: Deploy application correctly**
```bash
# Option 1: Copy WAR to Tomcat
cp target/lost-and-found.war $CATALINA_HOME/webapps/

# Option 2: Use Maven plugin
mvn tomcat:deploy

# Option 3: Deploy via Tomcat Manager
# http://localhost:8080/manager/html
```

**Step 5: Check Tomcat logs**
```bash
# Windows
type %CATALINA_HOME%\logs\catalina.out

# Linux/Mac
tail -f $CATALINA_HOME/logs/catalina.out
```

---

### Problem 6.2: JSP Blank Page or Weird Characters

**Error**: Page loads but shows nothing or corrupted characters

**Possible Causes**:
- Encoding issue
- JSP error not displaying
- Missing JSTL library

**Solutions**:

**Step 1: Add encoding to JSP**
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<!-- Add at top of every JSP file -->
```

**Step 2: Check JSTL dependency**
```xml
<!-- In pom.xml -->
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>javax.servlet.jsp.jstl</artifactId>
    <version>1.2.5</version>
</dependency>
```

**Step 3: Enable JSP error pages in web.xml**
```xml
<error-page>
    <error-code>500</error-code>
    <location>/WEB-INF/error.jsp</location>
</error-page>
```

**Step 4: Check browser console**
- Press F12
- Check Network tab for failed requests
- Check Console for JavaScript errors

---

### Problem 6.3: Form Submission Fails

**Error**: Submit button doesn't work or page refreshes

**Possible Causes**:
- Form action wrong
- Servlet not handling POST
- Validation failing silently

**Solutions**:

**Step 1: Verify form action**
```jsp
<!-- Check the form action path -->
<form method="post" action="${pageContext.request.contextPath}/items/post">
    <!-- Should be correct context path -->
</form>
```

**Step 2: Check servlet handles POST**
```java
@WebServlet("/items/post")
public class ItemServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // Must have doPost method
        System.out.println("Received POST request");
    }
}
```

**Step 3: Add debug logging**
```java
protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    System.out.println("=== POST REQUEST ===");
    System.out.println("Path: " + req.getServletPath());
    System.out.println("Title: " + req.getParameter("title"));
    System.out.println("Description: " + req.getParameter("description"));
    
    try {
        // Process request
        itemService.postItem(...);
        System.out.println("=== SUCCESS ===");
    } catch (Exception e) {
        System.out.println("=== ERROR ===");
        e.printStackTrace();
    }
}
```

**Step 4: Check browser network tab**
- F12 → Network
- Submit form
- Check if request was sent
- Check response status code

---

## Performance Issues

### Problem 7.1: Slow Page Load

**Error**: Pages take 5+ seconds to load

**Possible Causes**:
- N+1 query problem
- Large dataset queries
- Unoptimized database
- Network latency

**Solutions**:

**Step 1: Enable Hibernate SQL logging**
```xml
<!-- In hibernate.cfg.xml -->
<property name="hibernate.show_sql">true</property>
<property name="hibernate.format_sql">true</property>
<property name="hibernate.use_sql_comments">true</property>
```

**Step 2: Monitor actual queries in console**
```
Hibernate: SELECT COUNT(*) FROM users
Hibernate: SELECT COUNT(*) FROM items
```

**Step 3: Check for N+1 problem**
```java
// ✗ Bad: N+1 queries
List<Item> items = itemDAO.getAllOpenItems();  // 1 query
for (Item item : items) {
    User user = item.getUser();  // N queries (1 per item)
    System.out.println(user.getName());
}

// ✓ Good: Use eager loading or join
Query<Item> query = session.createQuery(
    "from Item i join fetch i.user where i.status='OPEN'",
    Item.class
);
```

**Step 4: Add database indexes**
```sql
-- Add indexes for frequent queries
CREATE INDEX idx_item_status ON items(status);
CREATE INDEX idx_item_type ON items(type);
CREATE INDEX idx_claim_status ON claims(status);
CREATE INDEX idx_user_email ON users(email);
```

**Step 5: Limit query results**
```java
// Add pagination
Query<Item> query = session.createQuery(
    "from Item where status='OPEN' order by id desc",
    Item.class
);
query.setFirstResult(0);
query.setMaxResults(10);  // Limit to 10 per page
```

---

### Problem 7.2: Memory Leak - Growing Heap Usage

**Error**: Application gets slower over time, eventually crashes

**Possible Causes**:
- Unclosed database sessions
- Session objects accumulating
- Large collections not cleared

**Solutions**:

**Step 1: Always close Hibernate sessions**
```java
// ✓ Good: Try-with-resources (Java 7+)
try (Session session = HibernateUtil.getSessionFactory().openSession()) {
    // Use session
} // Auto-closes

// ✓ Good: Explicit finally
Session session = HibernateUtil.getSessionFactory().openSession();
try {
    // Use session
} finally {
    session.close();
}

// ✗ Bad: Leaked session
Session session = HibernateUtil.getSessionFactory().openSession();
// Missing close()
```

**Step 2: Monitor heap usage**
```bash
# Start Tomcat with heap monitoring
export CATALINA_OPTS="-Xmx512m -Xms256m"
catalina.sh run

# Monitor with jvisualvm
jvisualvm
```

**Step 3: Check for accumulated collections**
```java
// ✗ Bad: Unbounded collection
static List<Item> cache = new ArrayList<>();

public void addToCache(Item item) {
    cache.add(item);  // Never cleared!
}

// ✓ Good: Bounded cache
Cache<Long, Item> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();
```

---

## Deployment Issues

### Problem 8.1: Application Won't Deploy to Tomcat

**Error**:
```
FAIL - Application already exists at context path /lost-and-found
or
ERROR - Error deploying application at context path
```

**Solutions**:

**Step 1: Stop Tomcat**
```bash
# Windows
catalina.bat stop

# Linux/Mac
catalina.sh stop
```

**Step 2: Remove old application**
```bash
# Remove WAR file
rm $CATALINA_HOME/webapps/lost-and-found.war

# Remove extracted directory
rm -rf $CATALINA_HOME/webapps/lost-and-found
```

**Step 3: Clear Tomcat work directory**
```bash
rm -rf $CATALINA_HOME/work/Catalina/localhost/lost-and-found
```

**Step 4: Rebuild and redeploy**
```bash
mvn clean install
cp target/lost-and-found.war $CATALINA_HOME/webapps/
```

**Step 5: Start Tomcat**
```bash
catalina.sh run
```

**Step 6: Check deployment in logs**
```bash
tail -f $CATALINA_HOME/logs/catalina.out
# Should see: "Deploying web application directory [lost-and-found]"
```

---

### Problem 8.2: Port 8080 Already in Use

**Error**:
```
java.net.BindException: Address already in use
```

**Solutions**:

**Step 1: Find process using port 8080**
```bash
# Windows
netstat -ano | findstr :8080
# Look for PID

# Linux/Mac
lsof -i :8080
# Shows process using port
```

**Step 2: Kill the process**
```bash
# Windows
taskkill /PID <PID> /F

# Linux/Mac
kill -9 <PID>
```

**Step 3: Or change Tomcat port**
```xml
<!-- In $CATALINA_HOME/conf/server.xml -->
<Connector port="8090" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />

<!-- Now access at http://localhost:8090/lost-and-found -->
```

---

## Common Bugs & Fixes

### Bug 8.1: Items Not Appearing After Post

**Symptom**: User posts item but it doesn't show in list

**Root Cause**: Item status not set to "OPEN" or item expires immediately

**Fix**:
```java
// In ItemService.postItem()
Item item = new Item();
item.setTitle(title);
item.setDescription(description);
item.setStatus("OPEN");  // ← Ensure this is set
item.setCreatedAt(new Date());
itemDAO.save(item);
```

**Verify in database**:
```sql
SELECT * FROM items WHERE status='OPEN';
```

---

### Bug 8.2: Claims Not Showing for Admin

**Symptom**: Admin sees "No claims" even after user submits

**Root Cause**: Claim status not set to "PENDING"

**Fix**:
```java
// In ClaimService.submitClaim()
Claim claim = new Claim();
claim.setStatus("PENDING");  // ← Ensure this is set
claim.setCreatedAt(new Date());
claimDAO.save(claim);
```

**Verify**:
```sql
SELECT * FROM claims WHERE status='PENDING';
```

---

### Bug 8.3: Images Disappear After Claim Approval

**Symptom**: Image URL shows but returns 404 after claim approved

**Root Cause**: Item marked as CLOSED, but image path still stored

**Fix**: This is actually not a bug - archived items keep images. But if image files deleted:
```bash
# Restore from backup
ls ~/lostfound/uploads/

# If empty, re-upload items
```

---

### Bug 8.4: Can't Login with Admin Credentials

**Symptom**: Admin credentials don't work, but user login does

**Root Cause**: Password hash mismatch

**Fix**:
```java
// Generate correct hash for "password"
String plain = "password";
String hashed = PasswordUtil.hashPassword(plain);
System.out.println("Hash: " + hashed);

// Verify it matches DB
System.out.println("Matches: " + 
    PasswordUtil.checkPassword(plain, 
        "$2a$10$7EqJtq98hPqEX7fNZaFWoOHiM8zY6P6KycdxY6SY3Y0JVpaz6RtZ2"));
```

**Update database**:
```sql
UPDATE users SET password='<new_hash>' 
WHERE email='admin@lostfound.com';
```

---

### Bug 8.5: Session Keeps Expiring

**Symptom**: User keeps getting logged out during use

**Root Cause**: Session timeout too short or session not being preserved

**Fix**:
```xml
<!-- In web.xml -->
<session-config>
    <cookie-config>
        <http-only>true</http-only>
        <secure>false</secure>  <!-- Set to true for HTTPS -->
    </cookie-config>
    <tracking-mode>COOKIE</tracking-mode>
    <!-- Tomcat default 30 min timeout -->
</session-config>
```

**Or in server.xml** (Tomcat):
```xml
<Context sessionCookiePath="/" sessionCookieDomain=".example.com"
         sessionTimeout="60">  <!-- 60 minutes -->
    <!-- ... -->
</Context>
```

---

## Debugging Techniques

### Using Logs

**Enable debug logging in console**:
```java
System.out.println("=== DEBUG: Starting item post ===");
System.out.println("User: " + user.getEmail());
System.out.println("Title: " + title);
```

**Check Tomcat logs**:
```bash
# Real-time
tail -f $CATALINA_HOME/logs/catalina.out

# Search for errors
grep ERROR $CATALINA_HOME/logs/catalina.out
```

### Using Browser Developer Tools

**F12 → Network Tab**:
1. Perform action
2. Check request method (GET/POST)
3. Check response status (200, 404, 500)
4. Check response body for error messages

**F12 → Console Tab**:
- Check for JavaScript errors
- Run custom JavaScript commands
- Monitor Network activity

### Using Database Tools

**MySQL Workbench**:
1. Connect to MySQL
2. Query tables directly
3. Monitor active connections
4. Check slow queries

**Command line**:
```sql
-- Check recent activity
SELECT * FROM items ORDER BY createdAt DESC LIMIT 5;
SELECT * FROM claims WHERE status='PENDING';
SELECT * FROM users WHERE email LIKE '%admin%';
```

---

## Quick Reference: Common Solutions

| Issue | Quick Fix |
|-------|-----------|
| **Can't login** | Verify admin user exists: `SELECT * FROM users` |
| **Images not showing** | Check upload dir: `~/lostfound/uploads/` |
| **404 error** | Check servlet mapping with `@WebServlet` |
| **Database connection error** | Verify MySQL running: `mysql -u root -p` |
| **Slow queries** | Enable SQL logging in hibernate.cfg.xml |
| **Port already in use** | Kill process: `lsof -i :8080` |
| **Session lost** | Clear cookies, check session timeout |
| **Claims not visible** | Check status='PENDING': `SELECT * FROM claims` |

---

**Last Updated**: April 22, 2026  
**Version**: 1.0  
**For Support**: Check project README or contact development team
