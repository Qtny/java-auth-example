package com.auth_example.auth_service.auth.models;

import com.auth_example.auth_service.mfa.MfaChallengeType;
import jakarta.validation.constraints.NotBlank;

public record MfaEmailInitiateRequest(
        @NotBlank(message = "email is a required filed")
        String email
) {
}
