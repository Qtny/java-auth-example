package com.auth_example.common_service.core.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private ApiErrorCode code;
    private String message;
}
