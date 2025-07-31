package com.auth_example.challenge_service.mfa.totp.models;

import com.auth_example.challenge_service.mfa.BaseMfaCreateRequest;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import jakarta.validation.constraints.NotBlank;

public record TotpCreateRequest(
        @NotBlank(message = "email is a required field") String email) implements BaseMfaCreateRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.TOTP;
    }
}
