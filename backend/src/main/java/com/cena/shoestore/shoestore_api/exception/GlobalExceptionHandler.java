package com.cena.shoestore.shoestore_api.exception;

import com.cena.shoestore.shoestore_api.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handleRuntimeException(Exception exception) {
        log.error("Exception: ", exception);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status("error")
                .code(String.valueOf(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode()))
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();

        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status("error")
                .code(String.valueOf(errorCode.getCode()))
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status("error")
                .code(String.valueOf(errorCode.getCode()))
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getFieldError();
        String message = Objects.nonNull(fieldError) ? fieldError.getDefaultMessage() : "Validation error";

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status("error")
                .code(String.valueOf(ErrorCode.INVALID_KEY.getCode()))
                .message(message)
                .build();

        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }
}
