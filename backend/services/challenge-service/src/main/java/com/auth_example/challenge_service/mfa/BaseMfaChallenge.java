package com.auth_example.challenge_service.mfa;

import java.time.LocalDate;
import java.util.UUID;

public interface BaseMfaChallenge {
    UUID id();
    MfaChallengeType type();
    LocalDate createdAt();
}
