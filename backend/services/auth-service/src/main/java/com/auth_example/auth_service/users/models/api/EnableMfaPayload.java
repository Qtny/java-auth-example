package com.auth_example.auth_service.users.models.api;

import com.auth_example.auth_service.mfa.MfaChallengeType;

public record EnableMfaPayload(
        String email,
        MfaChallengeType type,
        String target
) {
}
