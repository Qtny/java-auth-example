package com.auth_example.auth_service.mfa.models.totp;

import com.auth_example.auth_service.mfa.MfaChallengeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTotpMfaPayload(
        @NotBlank(message = "email is a required field")
        @Email(message = "email format is invalid")
        String email,
        @NotNull(message = "type is a required field")
        MfaChallengeType type
) {
}
