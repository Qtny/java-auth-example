package com.auth_example.auth_service.auth.models;

import com.auth_example.common_service.core.responses.ApiErrorCode;

public record MfaNotEnabledResponse(
        ApiErrorCode code,
        String message,
        String token
) {}
