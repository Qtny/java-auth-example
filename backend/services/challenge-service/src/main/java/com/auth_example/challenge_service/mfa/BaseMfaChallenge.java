package com.auth_example.challenge_service.mfa;

import java.time.LocalDate;
import java.util.UUID;

public interface BaseMfaChallenge {
    MfaChallengeType type();
    LocalDate createdAt();
}
