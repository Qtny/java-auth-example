package com.auth_example.auth_service.users;

public enum MfaMethod {
    SMS,
    EMAIL,
    TOTP,
    CAPTCHA,
    BIOMETRIC
}
