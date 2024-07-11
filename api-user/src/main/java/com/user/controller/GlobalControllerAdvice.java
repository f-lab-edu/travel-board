package com.user.controller;

import com.user.utils.error.ApplicationException;
import com.user.utils.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.user.utils.error.ErrorType.DEFAULT_ERROR;
import static com.user.utils.error.ErrorType.INVALID_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleTravelBoardException(ApplicationException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("ApplicationException : {}", e.getMessage(), e);
            case WARN -> log.warn("ApplicationException : {}", e.getMessage(), e);
            default -> log.info("ApplicationException : {}", e.getMessage(), e);
        }
        return ResponseEntity.status(e.getErrorType().getStatus())
                .body(ApiResponse.error(e.getErrorType(), e.getData()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException : {}", e.getMessage(), e);
        Map<String, String> validationData = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validationData.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ApiResponse.error(INVALID_REQUEST, validationData);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ApiResponse.error(DEFAULT_ERROR);
    }

}
