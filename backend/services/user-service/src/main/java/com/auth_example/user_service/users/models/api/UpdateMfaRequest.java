package com.auth_example.user_service.users.models.api;

import com.auth_example.user_service.users.MfaMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateMfaRequest(
        @NotBlank(message = "email is a required field")
        String email,
        @NotNull(message = "type is a required field")
        MfaMethod type,
        @NotNull(message = "target is a required field")
        String target
) {
}
