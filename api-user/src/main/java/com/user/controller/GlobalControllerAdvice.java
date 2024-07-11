package com.user.controller;

import com.user.utils.error.ApplicationException;
import com.user.utils.error.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.user.utils.error.ErrorType.DEFAULT_ERROR;
import static com.user.utils.error.ErrorType.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorMessage> handleTravelBoardException(ApplicationException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("ApplicationException : {}", e.getMessage(), e);
            case WARN -> log.warn("ApplicationException : {}", e.getMessage(), e);
            default -> log.info("ApplicationException : {}", e.getMessage(), e);
        }
        return ResponseEntity.status(e.getErrorType().getStatus())
                .body(new ErrorMessage(e.getErrorType(), e.getData()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException : {}", e.getMessage(), e);
        Map<String, String> validationData = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validationData.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(INVALID_REQUEST, validationData));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ErrorMessage(DEFAULT_ERROR));
    }

}
