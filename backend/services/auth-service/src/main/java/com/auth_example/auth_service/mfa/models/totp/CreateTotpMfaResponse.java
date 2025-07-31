package com.auth_example.auth_service.mfa.models.totp;

public record CreateTotpMfaResponse(
        String secret,
        String qrCodeUrl
) {
}
