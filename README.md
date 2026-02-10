# Spring Boot API Authentication Cookbook

A multi-module Spring Boot template project demonstrating various authentication and authorization patterns for RESTful APIs. This repository provides ready-to-use authentication implementations that can be installed and run independently.

## Overview

This project showcases different authentication mechanisms in Spring Boot, organized as separate runnable modules. Each module implements the same Journal CRUD API with different security configurations, allowing you to compare approaches and choose the right pattern for your needs.

## Project Structure

```
springboot-api-auth/
├── common/              # Shared domain entities, repositories, and DTOs
├── app-none/            # No authentication (open API)
├── app-basic/           # HTTP Basic Authentication
├── pom.xml              # Parent POM with dependency management
└── docker-compose.yml   # PostgreSQL database setup
```

## Technology Stack

- **Java:** 21
- **Spring Boot:** 4.0.0
- **Database:** PostgreSQL 16
- **Build Tool:** Maven
- **ORM:** Hibernate/JPA

## Available Authentication Modules

### 1. app-none (No Authentication)
- **Port:** 8080
- **Security:** None - all endpoints are public
- **Use Case:** Development baseline, public APIs
- **Documentation:** [app-none/README.md](app-none/README.md)

### 2. app-basic (HTTP Basic Authentication)
- **Port:** 8081
- **Security:** HTTP Basic with in-memory users
- **Authorization:** Role-based (USER, ADMIN)
- **Use Case:** Simple internal APIs, quick prototypes
- **Documentation:** [app-basic/README.md](app-basic/README.md)

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for PostgreSQL)

### 1. Start the Database

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 instance:
- **Host:** localhost:5432
- **Database:** journaldb
- **Username:** journaluser
- **Password:** journalpass

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run a Module

Run the no-auth module:
```bash
mvn spring-boot:run -pl app-none
```

Or run the basic-auth module:
```bash
mvn spring-boot:run -pl app-basic
```

## API Endpoints

All modules implement the same Journal CRUD API:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check endpoint |
| GET | `/journal` | List all journals |
| POST | `/journal` | Create a new journal |
| GET | `/journal/{id}` | Get a specific journal |
| PUT | `/journal/{id}` | Update a journal |
| DELETE | `/journal/{id}` | Delete a journal |

## Quick Test

### Test app-none (no authentication required):
```bash
curl http://localhost:8080/health
curl http://localhost:8080/journal
```

### Test app-basic (authentication required):
```bash
curl -u user:password http://localhost:8081/journal
curl -u admin:admin http://localhost:8081/journal
```

## Common Module

The `common` module contains shared code used by all authentication modules:

- **Journal Entity:** JPA entity with auto-managed timestamps
- **JournalRepository:** Spring Data JPA repository
- **DTOs:** Request/response objects with validation
  - `JournalCreateRequest`
  - `JournalUpdateRequest`

## Database Schema

The `journals` table is automatically created with:

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | Primary Key, Auto-increment |
| title | VARCHAR(255) | Not Null |
| content | TEXT | Not Null |
| created_at | TIMESTAMP | Not Null |
| updated_at | TIMESTAMP | Not Null |

## Error Handling

All modules implement RFC 7807 Problem Details for HTTP APIs:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Journal not found with id: 123"
}
```

## Development

### Adding a New Authentication Module

1. Create a new module directory: `app-{auth-type}/`
2. Add module to parent `pom.xml`
3. Create module `pom.xml` with dependency on `common`
4. Implement security configuration
5. Add README with usage examples

### Running Tests

```bash
mvn test
```

### Stopping the Database

```bash
docker-compose down
```

To remove the database volume:
```bash
docker-compose down -v
```

## Next Steps

This project is designed to be extended with additional authentication patterns. Planned implementations include:

- **JWT (JSON Web Tokens)** - Stateless authentication with token-based security
- **API Key** - Simple API key authentication for service-to-service communication
- **OAuth 2.0** - Industry-standard authorization framework with social login support

Each new authentication mechanism will be added as a separate module following the same structure, allowing you to compare implementations side-by-side.

## Contributing

Feel free to add new authentication patterns or improve existing implementations. Each module should:
- Be independently runnable
- Use the `common` module for shared code
- Include comprehensive documentation
- Follow Spring Boot best practices

## License

This project is a template for educational and development purposes.
