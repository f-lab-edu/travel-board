package com.user.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

public class ConflictException extends TravelBoardException {

    public ConflictException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return CONFLICT.value();
    }
}
