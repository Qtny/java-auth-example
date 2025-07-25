package com.auth_example.auth_service.mfa.models;

public record EmailValidateResponse(
        MfaChallengeType type,
        String target
) {
}
