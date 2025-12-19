package com.example.aggregator.client;

import com.example.aggregator.model.UserPreference;
import java.time.Duration;
import org.springframework.graphql.client.HttpGraphQlClient;
import reactor.core.publisher.Mono;

public class PreferenceGraphQlClient {

    private final HttpGraphQlClient client;

    public PreferenceGraphQlClient(HttpGraphQlClient client) {
        this.client = client;
    }

    public Mono<UserPreference> fetchPreferences(String userId) {
        String document = """
                query($userId: ID!) {
                    preferences(userId: $userId) {
                        marketingOptIn
                        theme
                    }
                }
                """;

        return client.document(document)
                .variable("userId", userId)
                .retrieve("preferences")
                .toEntity(UserPreference.class);
    }

    public UserPreference fetchPreferencesBlocking(String userId, Duration timeout) {
        return fetchPreferences(userId)
                .blockOptional(timeout)
                .orElseThrow(() -> new IllegalStateException("Preferences response was empty"));
    }
}
