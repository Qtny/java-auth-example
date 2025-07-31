package com.auth_example.auth_service.auth.models;

import jakarta.validation.constraints.NotBlank;

public record VerifyTotpMfaRequest(
        @NotBlank(message = "code is a required field")
        String code
) {
}
