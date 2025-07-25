package com.auth_example.challenge_service.mfa.models;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateChallengeRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,
        @NotNull(message = "type is required")
        MfaChallengeType type
) {
}
