package com.auth_example.user_service.users;

public enum UserStatus {
    PENDING_EMAIL_VERIFICATION,
    PENDING_MFA,
    ACTIVE,
    SUSPENDED,
    BANNED
}
