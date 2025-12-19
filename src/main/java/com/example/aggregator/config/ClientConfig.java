package com.example.aggregator.config;

import com.example.aggregator.client.PreferenceGraphQlClient;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean(name = "userServiceClient")
    public RestClient userServiceClient(AggregatorClientProperties properties) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(properties.userServiceBaseUrl().toString())
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "orderServiceClient")
    public RestClient orderServiceClient(AggregatorClientProperties properties) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(properties.orderServiceBaseUrl().toString())
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public PreferenceGraphQlClient preferenceGraphQlClient(AggregatorClientProperties properties) {
        WebClient webClient = WebClient.builder()
                .baseUrl(properties.preferenceGraphqlUrl().toString())
                .build();

        HttpGraphQlClient client = HttpGraphQlClient.builder(webClient).build();
        return new PreferenceGraphQlClient(client);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return requestFactory;
    }
}
