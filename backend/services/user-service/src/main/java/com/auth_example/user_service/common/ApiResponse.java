package com.auth_example.user_service.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean isSuccess;
    private T data;
    private Object error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

//    public static <T> ApiResponse<T> failure(String errorMessage) {
//        return new ApiResponse<>(false, null, errorMessage);
//    }

    public static <T> ApiResponse<T> failure(Object errors) {
        return new ApiResponse<>(false, null, errors);
    }
}
