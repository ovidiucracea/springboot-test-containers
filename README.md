# Data Aggregator with Spring Boot

This project demonstrates a production-ready Spring Boot 3.3 application that aggregates data from multiple downstream services. It uses:

- Spring MVC with the new `RestClient` for HTTP calls
- `HttpGraphQlClient` for GraphQL queries
- Virtual threads and structured concurrency on Java 21 for efficient parallel IO
- Actuator health probes and validation for robust operations

## How it works

The `ProfileController` exposes `GET /api/profiles/{userId}/summary`, which concurrently:

1. Fetches a user profile from a REST service
2. Fetches order history from a second REST service
3. Fetches user preferences from a GraphQL endpoint

Results are combined into a single JSON payload returned to the caller.

The aggregation work runs in virtual threads managed by Java's `StructuredTaskScope`,
which enforces a three-second deadline across all downstream calls. Each REST client
is configured with short connect/read timeouts to keep requests responsive even when
dependencies slow down.

## Running locally

1. Adjust downstream endpoints in `src/main/resources/application.yml`.
2. Build and run the application:

```bash
mvn spring-boot:run
```

## Testing

Run the lightweight Spring Boot context test with:

```bash
mvn test
```
