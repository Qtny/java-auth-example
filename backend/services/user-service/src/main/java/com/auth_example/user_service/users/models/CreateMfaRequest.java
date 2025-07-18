package com.auth_example.user_service.users.models;

import com.auth_example.user_service.users.MfaMethod;
import jakarta.validation.constraints.NotNull;

public record CreateMfaRequest(
        @NotNull(message = "target is required")
        String target,
        @NotNull(message = "method is required")
        MfaMethod method
) {
}
