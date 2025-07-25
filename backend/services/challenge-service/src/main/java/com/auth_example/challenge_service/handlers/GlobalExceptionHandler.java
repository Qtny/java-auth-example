package com.auth_example.challenge_service.handlers;

import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.EmailMismatchException;
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

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleChallengeNotFoundException(ChallengeNotFoundException exception) {
        ApiError error = new ApiError(ENTITY_NOT_FOUND, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(CodeDoesNotMatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleCodeDoesNotMatchException(CodeDoesNotMatchException exception) {
        ApiError error = new ApiError(MFA_CODE_INCORRECT, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(EmailMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailMismatchException(EmailMismatchException exception) {
        ApiError error = new ApiError(UNAUTHORIZED, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
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
