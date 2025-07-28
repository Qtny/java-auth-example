package com.auth_example.auth_service.users;

public enum UserStatus {
    PENDING_EMAIL_VERIFICATION,
    PENDING_MFA,
    ACTIVE,
    SUSPENDED,
    BANNED
}
