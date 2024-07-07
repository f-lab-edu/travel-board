package com.user.utils.error;

import lombok.Getter;

@Getter
public class ErrorMessage {

    private final String code;
    private final String message;
    private final Object data;

    public ErrorMessage(ErrorType errorType) {
        this.code = errorType.getCode().name();
        this.message = errorType.getMessage();
        this.data = null;
    }

    public ErrorMessage(ErrorType errorType, Object data) {
        this.code = errorType.getCode().name();
        this.message = errorType.getMessage();
        this.data = data;
    }
}
