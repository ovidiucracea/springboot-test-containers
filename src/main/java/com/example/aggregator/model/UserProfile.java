package com.example.aggregator.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfile(
        @NotBlank String id,
        @NotBlank String fullName,
        @Email String email) {
}
