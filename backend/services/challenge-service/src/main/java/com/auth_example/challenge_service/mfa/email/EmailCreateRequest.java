package com.auth_example.challenge_service.mfa.email;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.BaseMfaCreateRequest;

public record EmailCreateRequest(String email) implements BaseMfaCreateRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.EMAIL;
    }
}
