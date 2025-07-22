package com.auth_example.user_service.users;

public enum MfaMethod {
    SMS,
    EMAIL,
    TOTP,
    CAPTCHA,
    BIOMETRIC
}
