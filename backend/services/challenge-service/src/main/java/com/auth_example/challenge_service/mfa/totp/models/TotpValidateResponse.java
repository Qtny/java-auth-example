package com.auth_example.challenge_service.mfa.totp.models;

import com.auth_example.challenge_service.mfa.MfaChallengeType;

public record TotpValidateResponse(
        String email,
        MfaChallengeType type
) {
}
