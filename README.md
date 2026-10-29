# auth-service

Authentication microservice. Registers users and issues signed JWT tokens.
Spring Boot, WebFlux, RxJava 3, Spring Security (password hashing), MongoDB.

## Design decisions
- **Own database** (`auth_db`).
- **Passwords are stored hashed** with BCrypt, never in plain text.
- **Tokens are validated in the api-gateway**, not here. The gateway checks the
  signature with the shared secret, so it does not call this service on every
  request.
- **Same error for unknown user and wrong password** ("Invalid credentials"),
  so the response does not reveal which one failed.
- **Roles:** `ADMIN` can do everything; `USER` can read and operate but cannot
  delete (the gateway answers 403 to any DELETE with a USER token).

## Requirements
- JDK 17, Maven, MongoDB
- config-server (:8888), eureka-server (:8761)

## Run
1. Start MongoDB, config-server and eureka-server
2. `mvn spring-boot:run`
- Port: 8086 (from config-repo)
- Database: `auth_db`
- Swagger UI: http://localhost:8086/swagger-ui.html
- Through the gateway: http://localhost:8080/auth/... (public routes)

## Endpoints
| Method | Path | Description |
|---|---|---|
| POST | /auth/register | Register a user (role defaults to USER) |
| POST | /auth/login | Returns a Bearer token |

    POST /auth/register   { "username": "admin", "password": "admin123", "role": "ADMIN" }
    POST /auth/login      { "username": "admin", "password": "admin123" }

Use the token in every other request: `Authorization: Bearer <token>`.

## Error handling
| Status | Cause |
|---|---|
| 400 | Validation error or username already exists |
| 401 | Invalid credentials |

## Documentation
- OpenAPI contract: `docs/openapi.yaml`
- Sequence diagram: `docs/sequence-auth.puml`
- Architecture diagram: see the `bank-docs` repository

## Known limitations
- The signing secret lives in the config-repo (public). In production it must
  come from an environment variable or a secrets manager.
- Tokens cannot be revoked before they expire (no blacklist, no refresh tokens).
- `/auth/register` is public and lets anyone choose the ADMIN role. This is
  acceptable for the exercise; in production only an ADMIN should create admins.
- Services behind the gateway trust it: they do not validate the token again.
  They must not be exposed directly.

## Tests
`mvn clean test`. Coverage report: `target/site/jacoco/index.html`.