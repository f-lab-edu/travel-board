package com.user.utils.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static com.user.utils.error.ErrorCode.E400;
import static com.user.utils.error.ErrorCode.E409;
import static com.user.utils.error.ErrorCode.E500;
import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    DEFAULT_ERROR(INTERNAL_SERVER_ERROR, E500, "An unexpected error has occurred", ERROR),
    INVALID_REQUEST(BAD_REQUEST, E400, "Request validation failed", INFO),
    DUPLICATED_EMAIL(CONFLICT, E409, "Email is already in use", INFO);

    private final HttpStatus status;
    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;

}
