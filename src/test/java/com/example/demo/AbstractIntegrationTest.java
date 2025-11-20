package com.example.demo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.WireMockContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public abstract class AbstractIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("demo")
            .withUsername("demo")
            .withPassword("secret");

    @Container
    private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3"))
            .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.SNS);

    @Container
    private static final WireMockContainer WIREMOCK = new WireMockContainer(DockerImageName.parse("wiremock/wiremock:3.5.4"));

    static {
        POSTGRES.start();
        LOCALSTACK.start();
        WIREMOCK.start();
    }

    @BeforeEach
    void stubDefaultThirdPartyStatus() {
        WireMock wireMock = new WireMock(WIREMOCK.getHost(), WIREMOCK.getMappedPort(8080));
        wireMock.resetAll();
        wireMock.register(get(urlPathMatching("/orders/.*/status"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"READY\"}")));
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("aws.region", LOCALSTACK::getRegion);
        registry.add("aws.endpoint", () -> LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
        registry.add("aws.access-key", LOCALSTACK::getAccessKey);
        registry.add("aws.secret-key", LOCALSTACK::getSecretKey);

        registry.add("thirdparty.base-url", WIREMOCK::getBaseUrl);
    }

    protected static WireMockContainer wireMockContainer() {
        return WIREMOCK;
    }
}
