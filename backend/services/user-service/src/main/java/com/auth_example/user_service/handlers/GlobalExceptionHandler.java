package com.auth_example.user_service.handlers;

import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.user_service.exceptions.DuplicatedEmailException;
import com.auth_example.user_service.exceptions.UserMfaAlreadyEnabledException;
import com.auth_example.user_service.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.auth_example.common_service.core.responses.ApiErrorCode.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException exception) {
        ApiError error = new ApiError(ENTITY_NOT_FOUND, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(UserMfaAlreadyEnabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserMfaAlreadyEnabledException(UserMfaAlreadyEnabledException exception) {
        ApiError error = new ApiError(METHOD_NOT_ALLOWED, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicatedEmailException(DuplicatedEmailException exception) {
        ApiError error = new ApiError(METHOD_NOT_ALLOWED, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(error));
    }

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

        ApiError error = new ApiError(VALIDATION_ERROR, errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(error));
    }
}
