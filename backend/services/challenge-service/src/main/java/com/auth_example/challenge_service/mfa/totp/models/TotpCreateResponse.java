package com.auth_example.challenge_service.mfa.totp.models;

public record TotpCreateResponse(
        String secret,
        String qrCodeUrl
) {}
