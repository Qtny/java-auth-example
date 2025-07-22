package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMfaPayload(
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
        Address address,

        @NotNull(message = "type is required")
        MfaChallengeType type
) {
}
