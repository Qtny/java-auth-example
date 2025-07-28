package com.auth_example.auth_service.users.models.api;

import com.auth_example.auth_service.users.models.Address;

public record CreateUserPayload(
    String firstName,
    String lastName,
    String email,
    String password,
    Address address
) {
}
