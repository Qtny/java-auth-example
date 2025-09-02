package com.auth_example.challenge_service.handlers;

import com.auth_example.challenge_service.exceptions.*;
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

    @ExceptionHandler(TotpProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTotpProfileNotFoundException(TotpProfileNotFoundException exception) {
        ApiError error = new ApiError(ENTITY_NOT_FOUND, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(ErrorGeneratingQRException.class)
    public ResponseEntity<ApiResponse<Void>> handleErrorGeneratingQRException(ErrorGeneratingQRException exception) {
        ApiError error = new ApiError(ASSET_GENERATION_ERROR, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<ApiResponse<Void>> handleEncryptionException(EncryptionException exception) {
        ApiError error = new ApiError(INTERNAL_ERROR, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<ApiResponse<Void>> handleRedisException(RedisException exception) {
        ApiError error = new ApiError(INTERNAL_ERROR, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiError error = new ApiError(INTERNAL_ERROR, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(InvalidSkewException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSkewException(InvalidSkewException exception) {
        ApiError error = new ApiError(INTERNAL_ERROR, exception.getMessage());
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
