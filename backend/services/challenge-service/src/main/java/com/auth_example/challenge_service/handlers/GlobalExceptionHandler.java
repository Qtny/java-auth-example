package com.auth_example.challenge_service.handlers;

import com.auth_example.challenge_service.exceptions.UserAlreadyExistException;
import com.auth_example.common_service.core.exceptions.RemoteServiceException;
import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiErrorCode;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.exceptions.ServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.auth_example.common_service.core.responses.ApiErrorCode.ENTITY_ALREADY_EXIST;
import static com.auth_example.common_service.core.responses.ApiErrorCode.VALIDATION_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistException(UserAlreadyExistException exception) {
        ApiError error = new ApiError(ENTITY_ALREADY_EXIST, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleRemoteServiceException(RemoteServiceException exception) {
        ApiError error = exception.getApiError();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
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
