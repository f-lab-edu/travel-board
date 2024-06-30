package com.user.controller;

import com.user.controller.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final String VALIDATION_FAILED = "Validation failed";

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleException(RuntimeException e) {
        log.error("{}={}", INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), e);
        return ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> validations = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            validations.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ErrorResponse.of(BAD_REQUEST.value(), VALIDATION_FAILED, validations);
    }
}
