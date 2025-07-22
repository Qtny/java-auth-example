package com.auth_example.common_service.core.exceptions;

import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiErrorCode;

public class RemoteServiceException extends RuntimeException {
    private final ApiError apiError;

    public RemoteServiceException(ApiError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public ApiErrorCode getErrorCode() {
        return apiError.getCode();
    }
}
