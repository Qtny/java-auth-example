package com.auth_example.challenge_service.mfa.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerifyRequest(
        @NotBlank(message = "code is required")
        String code,
        @NotNull(message = "challengeId is required")
        UUID challengeId,
        @NotNull(message = "userId is required")
        UUID userId
) {
}
