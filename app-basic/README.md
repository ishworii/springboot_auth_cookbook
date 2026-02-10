# app-basic - HTTP Basic Authentication Module

A Spring Boot REST API with **HTTP Basic Authentication** and **role-based authorization**. This module demonstrates how to secure API endpoints using Spring Security with in-memory user credentials.

## Purpose

This module demonstrates:
- HTTP Basic Authentication with Spring Security
- Role-Based Access Control (RBAC)
- Method-level security with `@PreAuthorize`
- In-memory user management
- BCrypt password encoding

**Use Cases:**
- Internal APIs with simple authentication needs
- Quick prototypes requiring security
- Service-to-service communication
- Learning Spring Security fundamentals

## Port

**8081**

## Security

### Authentication
- **Type:** HTTP Basic Authentication
- **Header Format:** `Authorization: Basic <base64(username:password)>`
- **Password Encoding:** BCrypt

### Authorization
- **Model:** Role-Based Access Control (RBAC)
- **Roles:** USER, ADMIN

## Default Users

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| user | password | USER | Create, Read, Update journals |
| admin | admin | ADMIN | Create, Read, Update, Delete journals |

**Note:** These are in-memory users for demonstration. In production, use a database-backed user store.

## Getting Started

### Prerequisites

- Java 21+
- Maven
- PostgreSQL (via Docker Compose in root directory)

### 1. Start the Database

From the root directory:
```bash
docker-compose up -d
```

### 2. Run the Application

From the root directory:
```bash
mvn spring-boot:run -pl app-basic
```

Or from the module directory:
```bash
cd app-basic
mvn spring-boot:run
```

The application will start on port 8081.

## API Endpoints

### Health Check (Public)

```bash
GET /health
```

**Authentication:** Not required

**Example:**
```bash
curl http://localhost:8081/health
```

**Response:**
```json
"ok"
```

### List All Journals

```bash
GET /journal
```

**Authentication:** Required
**Roles:** USER, ADMIN

**Example:**
```bash
curl -u user:password http://localhost:8081/journal
# or
curl -u admin:admin http://localhost:8081/journal
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "My First Journal",
    "content": "Today was a great day!",
    "createdAt": "2026-02-10T10:30:00Z",
    "updatedAt": "2026-02-10T10:30:00Z"
  }
]
```

### Get Journal by ID

```bash
GET /journal/{id}
```

**Authentication:** Required
**Roles:** USER, ADMIN

**Example:**
```bash
curl -u user:password http://localhost:8081/journal/1
```

**Response:**
```json
{
  "id": 1,
  "title": "My First Journal",
  "content": "Today was a great day!",
  "createdAt": "2026-02-10T10:30:00Z",
  "updatedAt": "2026-02-10T10:30:00Z"
}
```

### Create Journal

```bash
POST /journal
Content-Type: application/json
```

**Authentication:** Required
**Roles:** USER, ADMIN

**Request Body:**
```json
{
  "title": "My Journal Title",
  "content": "Journal content goes here"
}
```

**Example:**
```bash
curl -u user:password -X POST http://localhost:8081/journal \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Journal",
    "content": "Today was a great day!"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "My First Journal",
  "content": "Today was a great day!",
  "createdAt": "2026-02-10T10:30:00Z",
  "updatedAt": "2026-02-10T10:30:00Z"
}
```

### Update Journal

```bash
PUT /journal/{id}
Content-Type: application/json
```

**Authentication:** Required
**Roles:** USER, ADMIN

**Example:**
```bash
curl -u user:password -X PUT http://localhost:8081/journal/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Journal Title",
    "content": "Updated content here"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Updated Journal Title",
  "content": "Updated content here",
  "createdAt": "2026-02-10T10:30:00Z",
  "updatedAt": "2026-02-10T11:45:00Z"
}
```

### Delete Journal

```bash
DELETE /journal/{id}
```

**Authentication:** Required
**Roles:** ADMIN only

**Example with USER (403 Forbidden):**
```bash
curl -u user:password -X DELETE http://localhost:8081/journal/1
```

**Response:**
```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access Denied"
}
```

**Example with ADMIN (200 OK):**
```bash
curl -u admin:admin -X DELETE http://localhost:8081/journal/1
```

**Response:**
```
(empty response)
```

## Security Configuration

### SecurityConfig.java

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health").permitAll()  // Public endpoint
                .anyRequest().authenticated()             // All others require auth
            )
            .httpBasic(Customizer.withDefaults());  // Enable HTTP Basic

        return http.build();
    }
}
```

### UserConfig.java

```java
@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Method-Level Security

The `JournalController` uses `@PreAuthorize` annotations:

```java
@RestController
@RequestMapping("/journal")
public class JournalController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Journal> getAllJournals() { ... }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Journal> createJournal(...) { ... }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // ADMIN only
    public ResponseEntity<Void> deleteJournal(...) { ... }
}
```

## HTTP Status Codes

| Status | Description | When |
|--------|-------------|------|
| 200 OK | Success | GET, PUT, DELETE successful |
| 201 Created | Resource created | POST successful |
| 400 Bad Request | Validation error | Invalid request body |
| 401 Unauthorized | Authentication failed | Invalid credentials or missing auth |
| 403 Forbidden | Authorization failed | Valid user but insufficient permissions |
| 404 Not Found | Resource not found | Journal ID doesn't exist |

## Error Responses

### 401 Unauthorized

**When:** No credentials or invalid credentials

**Example:**
```bash
curl http://localhost:8081/journal
```

**Response:**
```json
{
  "timestamp": "2026-02-10T12:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/journal"
}
```

### 403 Forbidden

**When:** Valid credentials but insufficient role

**Example:**
```bash
curl -u user:password -X DELETE http://localhost:8081/journal/1
```

**Response:**
```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access Denied"
}
```

### 404 Not Found

**When:** Journal doesn't exist

**Response:**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Journal not found with id: 999"
}
```

## Configuration

Configuration file: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: app-basic
  datasource:
    url: jdbc:postgresql://localhost:5432/journaldb
    username: journaluser
    password: journalpass
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8081
```

## Project Structure

```
app-basic/
├── src/main/java/com/ishwor/authcookbook/basic/
│   ├── AppBasicApplication.java         # Main entry point with @EnableMethodSecurity
│   ├── HealthController.java            # Health check endpoint
│   ├── journal/
│   │   ├── JournalController.java       # Journal CRUD with @PreAuthorize
│   │   └── JournalNotFoundException.java # Custom exception
│   ├── common/
│   │   └── ApiExceptionHandler.java     # Global exception handling
│   └── security/
│       ├── SecurityConfig.java          # HTTP Security configuration
│       └── UserConfig.java              # User details and password encoder
├── src/main/resources/
│   └── application.yml                  # Application configuration
└── pom.xml                              # Module dependencies
```

## Dependencies

From `pom.xml`:

```xml
<dependencies>
    <!-- Web framework -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Data access -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Security framework -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Shared domain/DTOs -->
    <dependency>
        <groupId>com.ishwor.authcookbook</groupId>
        <artifactId>common</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

## Testing

### Manual Testing with curl

```bash
# Health check (no auth needed)
curl http://localhost:8081/health

# Unauthorized access (401)
curl http://localhost:8081/journal

# Authorized access as USER
curl -u user:password http://localhost:8081/journal

# Create journal as USER
curl -u user:password -X POST http://localhost:8081/journal \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Journal","content":"Test content"}'

# Try to delete as USER (403 Forbidden)
curl -u user:password -X DELETE http://localhost:8081/journal/1

# Delete as ADMIN (200 OK)
curl -u admin:admin -X DELETE http://localhost:8081/journal/1
```

### Testing with Postman/Insomnia

**Base URL:** `http://localhost:8081`

**Authorization Tab:**
- Type: Basic Auth
- Username: `user` or `admin`
- Password: `password` or `admin`

**Test Scenarios:**

1. **As USER:**
   - GET /journal (200)
   - POST /journal (201)
   - PUT /journal/1 (200)
   - DELETE /journal/1 (403)

2. **As ADMIN:**
   - GET /journal (200)
   - POST /journal (201)
   - PUT /journal/1 (200)
   - DELETE /journal/1 (200)

3. **No Auth:**
   - GET /health (200)
   - GET /journal (401)

## Advanced Configuration

### Adding More Users

Modify `UserConfig.java`:

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails user1 = User.builder()
        .username("alice")
        .password(passwordEncoder().encode("alice123"))
        .roles("USER")
        .build();

    UserDetails user2 = User.builder()
        .username("bob")
        .password(passwordEncoder().encode("bob123"))
        .roles("USER", "ADMIN")  // Multiple roles
        .build();

    return new InMemoryUserDetailsManager(user1, user2);
}
```

### Custom Password Encoder

To use a different password encoder:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder();  // More secure alternative
}
```

### HTTPS Configuration

For production, enable HTTPS in `application.yml`:

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
```

### Database-Backed Users

For production, replace in-memory users with JPA:

1. Create `User` entity
2. Create `UserRepository`
3. Implement `UserDetailsService` with database lookup
4. Configure authentication provider

## Troubleshooting

### Authentication Always Fails

**Issue:** Credentials are correct but still get 401

**Solutions:**
- Ensure password encoding matches: BCrypt in both storage and validation
- Check username is exact match (case-sensitive)
- Verify `UserDetailsService` bean is loaded

### Wrong Role Rejected (403)

**Issue:** ADMIN user gets 403 on protected endpoint

**Solutions:**
- Ensure `@EnableMethodSecurity` is on main application class
- Check `@PreAuthorize` uses correct role name: `hasRole('ADMIN')` not `hasRole('ROLE_ADMIN')`
- Verify user has correct role in `UserDetailsService`

### CSRF Token Error

**Issue:** POST/PUT/DELETE requests fail with CSRF error

**Solution:**
CSRF is disabled in `SecurityConfig` for REST APIs. If you re-enable it, include CSRF token in requests.

## Security Best Practices

### Current Implementation (Development)
- Hardcoded credentials
- In-memory user storage
- Simple passwords
- BCrypt password encoding
- HTTPS recommended (not enforced)

### Production Recommendations
- Database-backed user storage
- Environment-based configuration
- Strong password policies
- HTTPS enforced
- Rate limiting
- Audit logging
- Session timeout configuration
- Regular security updates

## Migrating from app-none

If you're upgrading from the no-auth module:

1. Add Spring Security dependency
2. Create `SecurityConfig` and `UserConfig` classes
3. Add `@EnableMethodSecurity` to main application class
4. Add `@PreAuthorize` annotations to controller methods
5. Update API tests to include Basic Auth headers

No changes needed to:
- Database configuration
- Entity/Repository layer
- Business logic

## Next Steps

Explore more advanced authentication patterns:
- **JWT Module** (Planned) - Stateless token-based authentication
- **API Key Module** (Planned) - Simple key-based authentication
- **OAuth 2.0 Module** (Planned) - Social login and third-party authorization

For a completely open API, use the **app-none** module instead.
