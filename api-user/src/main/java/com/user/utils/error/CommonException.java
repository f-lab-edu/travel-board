package com.user.utils.error;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorType errorType;

    public CommonException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

}
