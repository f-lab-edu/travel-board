package com.user.utils.error;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorType errorType;
    private final Object data;

    public ApplicationException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = null;
    }

    public ApplicationException(ErrorType errorType, Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }
}
