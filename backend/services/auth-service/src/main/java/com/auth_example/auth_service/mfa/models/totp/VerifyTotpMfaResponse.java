package com.auth_example.auth_service.mfa.models.totp;

import com.auth_example.auth_service.mfa.MfaChallengeType;

public record VerifyTotpMfaResponse(
        String email,
        MfaChallengeType type
) {
}
