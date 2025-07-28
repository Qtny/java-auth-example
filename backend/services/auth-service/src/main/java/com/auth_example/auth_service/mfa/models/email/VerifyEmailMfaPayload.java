package com.auth_example.auth_service.mfa.models.email;

import java.util.UUID;

public record VerifyEmailMfaPayload(
        String email,
        UUID challengeId,
        String code
) {
}
