package com.auth_example.user_service.users.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull(message = "first name is required")
        String firstName,
        @NotNull(message = "last name is required")
        String lastName,
        @NotNull(message = "email is required")
        @Email(message = "email format is invalid")
        String email,
        @NotNull(message = "password is required")
        String password,
        Address address
) {
}
