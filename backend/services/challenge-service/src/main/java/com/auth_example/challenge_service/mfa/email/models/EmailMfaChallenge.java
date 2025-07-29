package com.auth_example.challenge_service.mfa.email.models;

import com.auth_example.challenge_service.mfa.BaseMfaChallenge;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import java.time.LocalDate;
import java.util.UUID;

public record EmailMfaChallenge(
        UUID id,
        String email,
        String code,
        LocalDate createdAt
) implements BaseMfaChallenge {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.EMAIL;
    }
}
