package com.auth_example.auth_service.users.models;

import java.util.UUID;

public record CreateUserPayload(
    String firstName,
    String lastName,
    String email,
    String password,
    Address address
) {
}
