package com.auth_example.user_service.users.models.api;

import com.auth_example.user_service.users.MfaMethod;
import com.auth_example.user_service.users.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "first name is required")
        String firstName,
        @NotBlank(message = "last name is required")
        String lastName,
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,
        @NotBlank(message = "password is required")
        String password,
        @Valid
        Address address
) {
}
