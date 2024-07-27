package com.user.dto.response;

import com.user.enums.ErrorType;
import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorMessage {

    private final int code;
    private final String message;
    private final Map<String, String> validations;

    public ErrorMessage(ErrorType errorType) {
        this.code = errorType.getStatus().value();
        this.message = errorType.getMessage();
        this.validations = Map.of();
    }

    public ErrorMessage(ErrorType errorType, Map<String, String> validations) {
        this.code = errorType.getStatus().value();
        this.message = errorType.getMessage();
        this.validations = validations;
    }
}
