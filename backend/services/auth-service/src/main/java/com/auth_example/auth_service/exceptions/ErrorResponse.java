package com.auth_example.auth_service.exceptions;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
