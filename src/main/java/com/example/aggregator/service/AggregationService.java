package com.example.aggregator.service;

import com.example.aggregator.client.PreferenceGraphQlClient;
import com.example.aggregator.model.AggregatedProfile;
import com.example.aggregator.model.OrderSummary;
import com.example.aggregator.model.UserPreference;
import com.example.aggregator.model.UserProfile;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AggregationService {

    private static final Logger log = LoggerFactory.getLogger(AggregationService.class);
    private static final Duration AGGREGATION_TIMEOUT = Duration.ofSeconds(3);

    private final RestClient userServiceClient;
    private final RestClient orderServiceClient;
    private final PreferenceGraphQlClient preferenceGraphQlClient;

    public AggregationService(@Qualifier("userServiceClient") RestClient userServiceClient,
                              @Qualifier("orderServiceClient") RestClient orderServiceClient,
                              PreferenceGraphQlClient preferenceGraphQlClient) {
        this.userServiceClient = userServiceClient;
        this.orderServiceClient = orderServiceClient;
        this.preferenceGraphQlClient = preferenceGraphQlClient;
    }

    public AggregatedProfile aggregateProfile(String userId) {
        Instant deadline = Instant.now().plus(AGGREGATION_TIMEOUT);

        try (var scope = new StructuredTaskScope.ShutdownOnFailure("aggregate-profile", Thread.ofVirtual().factory())) {
            StructuredTaskScope.Subtask<UserProfile> userTask = scope.fork(() -> fetchUserProfile(userId));
            StructuredTaskScope.Subtask<List<OrderSummary>> ordersTask = scope.fork(() -> fetchOrders(userId));
            StructuredTaskScope.Subtask<UserPreference> preferencesTask = scope.fork(() -> fetchPreferences(userId));

            scope.joinUntil(deadline);
            scope.throwIfFailed(DownstreamServiceException::new);

            return new AggregatedProfile(userTask.get(), ordersTask.get(), preferencesTask.get());
        } catch (TimeoutException e) {
            log.warn("Aggregation timed out after {} for user {}", AGGREGATION_TIMEOUT, userId);
            throw new DownstreamServiceException("Aggregation timed out", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DownstreamServiceException("Aggregation interrupted", e);
        }
    }

    private UserProfile fetchUserProfile(String userId) {
        log.debug("Fetching user profile for {}", userId);
        return userServiceClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .body(UserProfile.class);
    }

    private List<OrderSummary> fetchOrders(String userId) {
        log.debug("Fetching orders for {}", userId);
        return orderServiceClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders").queryParam("userId", userId).build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrderSummary>>() {});
    }

    private UserPreference fetchPreferences(String userId) {
        log.debug("Fetching preferences for {}", userId);
        return preferenceGraphQlClient.fetchPreferencesBlocking(userId, AGGREGATION_TIMEOUT);
    }
}
