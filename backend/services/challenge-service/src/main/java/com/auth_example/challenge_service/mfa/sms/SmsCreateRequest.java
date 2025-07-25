package com.auth_example.challenge_service.mfa.sms;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.BaseMfaCreateRequest;

public record SmsCreateRequest(String phoneNumber) implements BaseMfaCreateRequest {
    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.SMS;
    }
}
