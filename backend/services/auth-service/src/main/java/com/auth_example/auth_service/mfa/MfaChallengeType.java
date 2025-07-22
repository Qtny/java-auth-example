package com.auth_example.auth_service.mfa;

public enum MfaChallengeType {
    SMS,
    EMAIL,
    TOTP,
    CAPTCHA,
    BIOMETRIC
}
