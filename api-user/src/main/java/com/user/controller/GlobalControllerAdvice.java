package com.user.controller;

import com.user.utils.error.ErrorType;
import com.user.utils.error.TravelBoardException;
import com.user.utils.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(TravelBoardException.class)
    public ResponseEntity<ApiResponse<Void>> handleTravelBoardException(TravelBoardException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("TravelBoardException : {}", e.getMessage(), e);
            case WARN -> log.warn("TravelBoardException : {}", e.getMessage(), e);
            default -> log.info("TravelBoardException : {}", e.getMessage(), e);
        }
        return ResponseEntity.status(e.getErrorType().getStatus())
                .body(ApiResponse.error(e.getErrorType(), e.getData()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException : {}", e.getMessage(), e);
        Map<String, String> validationData = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validationData.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.getStatus())
                .body(ApiResponse.error(ErrorType.INVALID_REQUEST, validationData));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.status(ErrorType.DEFAULT_ERROR.getStatus())
                .body(ApiResponse.error(ErrorType.DEFAULT_ERROR));
    }

}
