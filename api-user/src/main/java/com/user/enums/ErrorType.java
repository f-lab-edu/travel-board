package com.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, "An unexpected error has occurred", ERROR),
    INVALID_REQUEST(BAD_REQUEST, "Request validation failed", INFO),
    DUPLICATED_EMAIL(CONFLICT, "Email is already in use", INFO),
    UNAUTHORIZED_TOKEN(UNAUTHORIZED, "Unauthorized token", INFO),
    LOGIN_FAIL(UNAUTHORIZED, "Invalid email or password", INFO),
    USER_NOT_FOUND(UNAUTHORIZED, "User not found", INFO);

    private final HttpStatus status;
    private final String message;
    private final LogLevel logLevel;

}
