package com.auth_example.challenge_service.mfa.totp.models;

import com.auth_example.challenge_service.mfa.BaseMfaFindRequest;
import com.auth_example.challenge_service.mfa.MfaChallengeType;

public record TotpMfaFindByEmailRequest(
    String email
) implements BaseMfaFindRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.TOTP;
    }
}
