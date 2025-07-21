package com.auth_example.auth_service.handlers;

import com.auth_example.auth_service.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(UserMfaAlreadyEnabledException.class)
//    public ResponseEntity<ApiResponse<Void>> handleUserMfaAlreadyEnabledException(UserMfaAlreadyEnabledException exception) {
//        return ResponseEntity
//                .status(HttpStatus.METHOD_NOT_ALLOWED)
//                .body(ApiResponse.failure(exception.getMessage()));
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException exception)
    {
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Invalid input");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(errorMessage));
    }
}
