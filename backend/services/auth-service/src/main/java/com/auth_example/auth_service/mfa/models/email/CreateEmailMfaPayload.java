package com.auth_example.auth_service.mfa.models.email;

import com.auth_example.auth_service.auth.models.Address;
import com.auth_example.auth_service.mfa.MfaChallengeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateEmailMfaPayload(
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,
        @NotBlank(message = "type is a required field")
        MfaChallengeType type
) {
}
