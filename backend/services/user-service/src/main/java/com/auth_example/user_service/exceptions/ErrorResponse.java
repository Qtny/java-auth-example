package com.auth_example.user_service.exceptions;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
