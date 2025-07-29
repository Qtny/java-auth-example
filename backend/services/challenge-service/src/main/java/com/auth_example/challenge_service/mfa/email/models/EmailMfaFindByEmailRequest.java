package com.auth_example.challenge_service.mfa.email.models;

import com.auth_example.challenge_service.mfa.BaseMfaFindRequest;
import com.auth_example.challenge_service.mfa.MfaChallengeType;

public record EmailMfaFindByEmailRequest(
        String email
) implements BaseMfaFindRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.EMAIL;
    }
}
