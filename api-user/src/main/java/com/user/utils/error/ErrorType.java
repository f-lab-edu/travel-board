package com.user.utils.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, "An unexpected error has occurred", ERROR),
    INVALID_REQUEST(BAD_REQUEST, "Request validation failed", INFO),
    DUPLICATED_EMAIL(CONFLICT, "Email is already in use", INFO),
    USER_NOT_FOUND(NOT_FOUND, "User not found", INFO),
    LOGIN_FAIL(UNAUTHORIZED, "Email and password are incorrect", INFO),
    TOKEN_EXPIRED(UNAUTHORIZED, "Token has expired", INFO),
    INVALID_TOKEN(UNAUTHORIZED, "Token is invalid", INFO),
    UNAUTHORIZED_TOKEN(UNAUTHORIZED, "Unauthorized token", INFO);

    private final HttpStatus status;
    private final String message;
    private final LogLevel logLevel;

}
