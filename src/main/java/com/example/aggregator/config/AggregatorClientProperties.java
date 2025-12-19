package com.example.aggregator.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aggregator.clients")
public record AggregatorClientProperties(
        URI userServiceBaseUrl,
        URI orderServiceBaseUrl,
        URI preferenceGraphqlUrl) {
}
