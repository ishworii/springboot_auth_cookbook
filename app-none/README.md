# app-none - No Authentication Module

A Spring Boot REST API with **no authentication or authorization**. This module provides a baseline implementation of the Journal CRUD API with all endpoints publicly accessible.

## Purpose

This module demonstrates:
- Basic Spring Boot REST API structure
- CRUD operations with Spring Data JPA
- Validation and error handling
- API best practices without security complexity

**Use Cases:**
- Development and testing baseline
- Public APIs with no sensitive data
- Rapid prototyping
- Learning Spring Boot fundamentals

## Port

**8080**

## Security

No authentication or authorization. All endpoints are publicly accessible.

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
mvn spring-boot:run -pl app-none
```

Or from the module directory:
```bash
cd app-none
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### Health Check

```bash
GET /health
```

**Response:**
```json
"ok"
```

**Example:**
```bash
curl http://localhost:8080/health
```

### List All Journals

```bash
GET /journal
```

**Response:** Array of journal objects

**Example:**
```bash
curl http://localhost:8080/journal
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

**Example:**
```bash
curl http://localhost:8080/journal/1
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

**Error Response (404):**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Journal not found with id: 999"
}
```

### Create Journal

```bash
POST /journal
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "My Journal Title",
  "content": "Journal content goes here"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/journal \
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

**Validation Rules:**
- `title`: Required, max 255 characters
- `content`: Required

**Validation Error Response (400):**
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "title: must not be blank"
}
```

### Update Journal

```bash
PUT /journal/{id}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Updated Title",
  "content": "Updated content"
}
```

**Example:**
```bash
curl -X PUT http://localhost:8080/journal/1 \
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

**Note:** The `updatedAt` timestamp is automatically updated.

### Delete Journal

```bash
DELETE /journal/{id}
```

**Example:**
```bash
curl -X DELETE http://localhost:8080/journal/1
```

**Response (200 OK):**
```
(empty response)
```

## Configuration

Configuration file: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: app-none
  datasource:
    url: jdbc:postgresql://localhost:5432/journaldb
    username: journaluser
    password: journalpass
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8080
```

### Database Configuration

- **URL:** jdbc:postgresql://localhost:5432/journaldb
- **Username:** journaluser
- **Password:** journalpass
- **DDL Auto:** update (creates/updates schema automatically)

## Project Structure

```
app-none/
├── src/main/java/com/ishwor/authcookbook/none/
│   ├── AppNoneApplication.java          # Main application entry point
│   ├── HealthController.java            # Health check endpoint
│   ├── journal/
│   │   ├── JournalController.java       # Journal CRUD endpoints
│   │   └── JournalNotFoundException.java # Custom exception
│   └── common/
│       └── ApiExceptionHandler.java     # Global exception handling
├── src/main/resources/
│   └── application.yml                  # Application configuration
└── pom.xml                              # Module dependencies
```

## Key Components

### JournalController

REST controller providing CRUD operations:
- Uses constructor injection for `JournalRepository`
- Implements proper HTTP status codes
- Delegates to Spring Data JPA for data access

### ApiExceptionHandler

Global exception handler using `@RestControllerAdvice`:
- Handles `JournalNotFoundException` → 404 Not Found
- Handles validation errors → 400 Bad Request
- Returns RFC 7807 Problem Details format

### Journal Entity (from common module)

JPA entity with automatic timestamp management:
- `id`: Auto-generated primary key
- `title`: String, max 255 characters
- `content`: Text field
- `createdAt`: Set automatically on creation
- `updatedAt`: Updated automatically on modification

## Testing

### Manual Testing with curl

```bash
# Health check
curl http://localhost:8080/health

# Create a journal
curl -X POST http://localhost:8080/journal \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Journal","content":"Test content"}'

# List all journals
curl http://localhost:8080/journal

# Get specific journal
curl http://localhost:8080/journal/1

# Update journal
curl -X PUT http://localhost:8080/journal/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","content":"Updated content"}'

# Delete journal
curl -X DELETE http://localhost:8080/journal/1
```

### Testing with Postman/Insomnia

Import the following collection:

**Base URL:** `http://localhost:8080`

| Method | Endpoint | Body |
|--------|----------|------|
| GET | `/health` | - |
| GET | `/journal` | - |
| POST | `/journal` | `{"title":"...","content":"..."}` |
| GET | `/journal/1` | - |
| PUT | `/journal/1` | `{"title":"...","content":"..."}` |
| DELETE | `/journal/1` | - |

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

    <!-- Shared domain/DTOs -->
    <dependency>
        <groupId>com.ishwor.authcookbook</groupId>
        <artifactId>common</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

## Troubleshooting

### Port Already in Use

If port 8080 is already in use, change it in `application.yml`:
```yaml
server:
  port: 8090  # or any available port
```

### Database Connection Failed

Ensure PostgreSQL is running:
```bash
docker-compose ps
```

If not running:
```bash
docker-compose up -d
```

### Tables Not Created

Check Hibernate DDL setting in `application.yml`:
```yaml
spring.jpa.hibernate.ddl-auto: update
```

For debugging, enable SQL logging:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

## Next Steps

To add authentication to this module's functionality, explore:
- **app-basic** - HTTP Basic Authentication with role-based access control
- Future modules: JWT, API Key, OAuth 2.0

## Security Considerations

**Warning:** This module has NO security. Do not use in production or for sensitive data:
- All data is publicly readable
- Anyone can create, modify, or delete journals
- No rate limiting or abuse prevention
- No audit logging

For production use, implement one of the secured authentication modules.
