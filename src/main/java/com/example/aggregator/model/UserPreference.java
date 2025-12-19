package com.example.aggregator.model;

import jakarta.validation.constraints.NotBlank;

public record UserPreference(
        boolean marketingOptIn,
        @NotBlank String theme) {
}
