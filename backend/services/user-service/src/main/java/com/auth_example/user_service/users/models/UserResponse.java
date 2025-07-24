package com.auth_example.user_service.users.models;

import com.auth_example.user_service.users.UserStatus;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String firstName,
    String lastName,
    boolean mfaEnabled,
    UserStatus status,
    Address address
) {
}
