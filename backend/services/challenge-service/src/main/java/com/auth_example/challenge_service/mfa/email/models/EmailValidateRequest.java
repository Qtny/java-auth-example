package com.auth_example.challenge_service.mfa.email.models;

import com.auth_example.challenge_service.mfa.BaseMfaValidateRequest;

import java.util.UUID;

public record EmailValidateRequest(
        UUID challengeId,
        String code,
        String email
) implements BaseMfaValidateRequest {}
