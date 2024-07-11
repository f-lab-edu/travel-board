package com.user.controller;

import com.user.utils.error.CommonException;
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

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorMessage> handleCommonException(CommonException e) {
        ErrorMessage message = new ErrorMessage(e.getErrorType());
        return ResponseEntity.status(e.getErrorType().getStatus()).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> validations = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validations.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorMessage message = new ErrorMessage(INVALID_REQUEST, validations);
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException e) {
        log.error("Exception : {}", e.getMessage(), e);
        ErrorMessage message = new ErrorMessage(DEFAULT_ERROR);
        return ResponseEntity.internalServerError().body(message);
    }

}
