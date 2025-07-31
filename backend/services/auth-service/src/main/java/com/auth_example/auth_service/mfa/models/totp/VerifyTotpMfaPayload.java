package com.auth_example.auth_service.mfa.models.totp;

public record VerifyTotpMfaPayload(
        String email,
        String code
) {
}
