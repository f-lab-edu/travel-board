package com.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidRequestException extends TravelBoardException {

    private final String field;

    public InvalidRequestException(String field, String message) {
        super(message);
        this.field = field;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
