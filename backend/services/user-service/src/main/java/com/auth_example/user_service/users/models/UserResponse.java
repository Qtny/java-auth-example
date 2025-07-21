package com.auth_example.user_service.users.models;

import com.auth_example.user_service.users.UserStatus;

public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    boolean mfaEnabled,
    UserStatus status,
    Address address
) {
}
