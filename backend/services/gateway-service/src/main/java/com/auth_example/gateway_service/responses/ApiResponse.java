package com.auth_example.gateway_service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean isSuccess;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(T data) {
        return new ApiResponse<>(false, data, null);
    }

    public static <T> ApiResponse<T> error(ApiError errors) {
        return new ApiResponse<>(false, null, errors);
    }
}
