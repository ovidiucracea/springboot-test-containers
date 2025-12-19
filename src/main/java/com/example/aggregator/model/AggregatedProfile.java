package com.example.aggregator.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AggregatedProfile(
        @NotNull @Valid UserProfile user,
        @Valid List<OrderSummary> orders,
        @NotNull @Valid UserPreference preference) {
}
