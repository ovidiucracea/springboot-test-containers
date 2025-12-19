package com.example.aggregator.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderSummary(
        @NotBlank String id,
        @PositiveOrZero BigDecimal total,
        @NotBlank String status,
        @NotNull Instant placedAt) {
}
