package com.auth_example.auth_service.handlers;

import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.auth_service.exceptions.EmailAlreadyExistException;
import com.auth_example.common_service.core.exceptions.RemoteServiceException;
import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.auth_example.common_service.core.responses.ApiErrorCode.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistException(EmailAlreadyExistException exception) {
        ApiError error = new ApiError(ENTITY_ALREADY_EXIST, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.failure(error));
    }

    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleRemoteServiceException(RemoteServiceException exception) {
        ApiError error = exception.getApiError();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(error));
    }

    @ExceptionHandler(ApiNotSuccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiNotSuccessException(ApiNotSuccessException exception) {
        ApiError error = new ApiError(ENTITY_NOT_FOUND, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.failure(error));
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
                .body(ApiResponse.failure(error));
    }
}
