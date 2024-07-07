package com.user.utils.error;

import lombok.Getter;

@Getter
public class TravelBoardException extends RuntimeException {

    private final ErrorType errorType;

    private final Object data;

    public TravelBoardException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = null;
    }

    public TravelBoardException(ErrorType errorType, Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }
}
