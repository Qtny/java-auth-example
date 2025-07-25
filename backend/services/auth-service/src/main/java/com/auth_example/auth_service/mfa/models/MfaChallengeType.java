package com.auth_example.auth_service.mfa.models;

public enum MfaChallengeType {
    SMS,
    EMAIL,
    TOTP,
    CAPTCHA,
    BIOMETRIC
}
