package com.auth_example.auth_service.auth.models;

public record TotpMfaInitiateResponse(
        String secret,
        String qrCodeUrl
) {
}
