package com.auth_example.challenge_service.mfa.totp.models;

import com.auth_example.challenge_service.mfa.BaseMfaValidateRequest;

import java.util.UUID;

public record TotpValidateRequest(
        UUID challengeId,
        String email,
        String code
) implements BaseMfaValidateRequest {}
