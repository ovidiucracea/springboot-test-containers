# springboot-test-containers

This sample shows how to share long-lived Testcontainers instances across Spring Boot integration tests.

## Container lifecycle
- `AbstractIntegrationTest` declares PostgreSQL and LocalStack containers as `static @Container` fields and starts them in a static block. That means they are started once per JVM and reused by every test class, avoiding container recreation for each test method or class.
- The containers still shut down when the JVM exits. You do not need to call `.withReuse(true)` for this intra-run reuse; that flag only matters when you want to reuse containers across JVM invocations (and requires `~/.testcontainers.properties` to enable reuse).
- Because tests target container endpoints via Spring's dynamic properties, no additional reuse configuration is required for the sample to keep the same containers during the test suite.
- The same base class also exposes a reusable WireMock container for stubbing third-party REST calls; the `ThirdPartyApiIntegrationTest` class demonstrates registering a stub via the container's mapped port and asserting the client call against the stubbed endpoint.

## Running tests
Run the integration tests with

```bash
mvn test
```

If external Maven repositories are blocked, you may need network access to download dependencies on the first run.
