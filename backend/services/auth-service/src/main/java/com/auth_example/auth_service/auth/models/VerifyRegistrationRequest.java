package com.auth_example.auth_service.auth.models;

import jakarta.validation.constraints.NotBlank;

public record VerifyRegistrationRequest(
        @NotBlank(message = "code is required")
        String code,
        @NotBlank(message = "challengeId is required")
        String challengeId
) {
}
