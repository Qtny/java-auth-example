package com.auth_example.auth_service.auth.models;

import com.auth_example.auth_service.mfa.MfaChallengeType;

public record LoginResponse(
        String token
) {
}
