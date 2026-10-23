# Blog Management API

A Spring Boot 3 REST API for managing posts, categories, and moderated comments.

## Requirements

- Java 17 or later
- Maven 3.6 or later

## Run locally

```powershell
mvn spring-boot:run
```

The default `dev` profile uses an in-memory H2 database. The H2 console is available at
`http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:blogdb`, username: `sa`, no password).

Run the integration tests with `mvn test`. To use the test profile explicitly, set
`SPRING_PROFILES_ACTIVE=test`.

## Profiles

- `dev` (default): in-memory H2, schema updates, SQL logging, and the H2 console.
- `test`: isolated in-memory H2 database with a create/drop schema.
- `prod`: PostgreSQL; provide `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`.
  For example, `DATABASE_URL` should be a JDBC URL such as
  `jdbc:postgresql://localhost:5432/blog`. Production schema changes are not applied
  automatically (`ddl-auto=validate`).

Select a profile with `SPRING_PROFILES_ACTIVE`, for example `prod`.

## API documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Postman collection: [`postman/Blog-Management-API.postman_collection.json`](postman/Blog-Management-API.postman_collection.json)

## Endpoints

| Resource | Endpoints |
| --- | --- |
| Posts | `GET/POST /api/posts`, `GET/PUT/DELETE /api/posts/{id}` |
| Categories | `GET/POST /api/categories`, `GET/PUT/DELETE /api/categories/{id}` |
| Post comments | `GET/POST /api/posts/{postId}/comments` |
| Comments | `GET/PUT/DELETE /api/comments/{id}`, `PATCH /api/comments/{id}/moderation` |

Post listing supports `page`, `size` (maximum 100), `sort`, `categoryId`, and `author`
query parameters. Comments are unapproved when created. Deleting a post also deletes
its comments; deleting a category that is still assigned to posts returns `409 Conflict`.

Successful creates return `201 Created`, deletes return `204 No Content`, and errors
return a structured JSON response with an appropriate status and validation details.
