package com.auth_example.auth_service.auth.models;

import com.auth_example.auth_service.mfa.MfaChallengeType;

public record LoginMfaResponse(
        String token,
        MfaChallengeType type,
        String challengeId
) {
}
