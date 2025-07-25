package com.auth_example.auth_service.mfa.models;

import java.util.UUID;

public record VerifyMfaPayload(
        String email,
        UUID challengeId,
        String code
) {
}
