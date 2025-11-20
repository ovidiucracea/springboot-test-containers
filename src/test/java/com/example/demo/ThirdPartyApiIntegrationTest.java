package com.example.demo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.ThirdPartyResponse;
import com.example.demo.service.ThirdPartyClient;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ThirdPartyApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ThirdPartyClient thirdPartyClient;

    @Test
    void fetchesOrderStatusViaWireMockContainer() {
        WireMock wireMock = new WireMock(
                wireMockContainer().getHost(),
                wireMockContainer().getMappedPort(8080));

        wireMock.register(get(urlEqualTo("/orders/42/status"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"READY\"}")));

        ThirdPartyResponse response = thirdPartyClient.fetchOrderStatus("42");

        assertThat(response.status()).isEqualTo("READY");
    }
}
