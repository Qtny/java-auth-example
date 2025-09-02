package com.auth_example.challenge_service.mfa.email.models;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.BaseMfaCreateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCreateRequest(
        @Email
        @NotBlank
        String email
) implements BaseMfaCreateRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.EMAIL;
    }
}
