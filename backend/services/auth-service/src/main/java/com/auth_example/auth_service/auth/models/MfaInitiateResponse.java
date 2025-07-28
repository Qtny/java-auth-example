package com.auth_example.auth_service.auth.models;

import java.util.UUID;

public record MfaInitiateResponse(
        String challengeId
) {
}
