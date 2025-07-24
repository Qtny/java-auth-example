package com.auth_example.auth_service.mfa.models;

import java.util.UUID;

public record CreateMfaResponse(
        UUID challengeId,
        UUID userId
) {
}
