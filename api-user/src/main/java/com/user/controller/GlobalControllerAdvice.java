package com.user.controller;

import com.user.utils.error.ErrorType;
import com.user.utils.error.TravelBoardException;
import com.user.utils.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(TravelBoardException.class)
    public ResponseEntity<ApiResponse<?>> handleCoreApiException(TravelBoardException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("TravelBoardException : {}", e.getMessage(), e);
            case WARN -> log.warn("TravelBoardException : {}", e.getMessage(), e);
            default -> log.info("TravelBoardException : {}", e.getMessage(), e);
        }
        ApiResponse<?> body = ApiResponse.error(e.getErrorType(), e.getData());
        return ResponseEntity.status(e.getErrorType().getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.status(ErrorType.DEFAULT_ERROR.getStatus())
                .body(ApiResponse.error(ErrorType.DEFAULT_ERROR));
    }

}
