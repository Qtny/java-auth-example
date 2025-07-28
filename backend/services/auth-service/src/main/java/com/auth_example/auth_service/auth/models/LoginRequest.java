package com.auth_example.auth_service.auth.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "format is not a valid email format")
        @NotBlank(message = "email is a required field")
        String email,
        @NotBlank(message = "password is a required field")
        String password
) {
}
