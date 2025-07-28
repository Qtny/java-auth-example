package com.auth_example.auth_service.mfa.models.email;

import com.auth_example.auth_service.mfa.MfaChallengeType;

public record EmailValidateResponse(
        String email,
        MfaChallengeType type
) {
}
