package com.auth_example.challenge_service.mfa.email;

import com.auth_example.challenge_service.mfa.BaseMfaValidateResponse;
import com.auth_example.challenge_service.mfa.MfaChallengeType;

public record EmailValidateResponse(
        MfaChallengeType type,
        String email
) implements BaseMfaValidateResponse {}
