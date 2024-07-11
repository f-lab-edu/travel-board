package com.user.utils.error;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorType errorType;

    public ApplicationException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

}
