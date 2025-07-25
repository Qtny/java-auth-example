package com.auth_example.challenge_service.mfa;

import java.util.UUID;

public interface BaseMfaValidateRequest {
    UUID challengeId();
}
