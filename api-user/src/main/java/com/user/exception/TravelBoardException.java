package com.user.exception;

public abstract class TravelBoardException extends RuntimeException {

    public TravelBoardException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}
