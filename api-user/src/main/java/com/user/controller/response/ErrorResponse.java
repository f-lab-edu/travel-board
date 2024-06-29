package com.user.controller.response;

import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> validations
) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Map.of());
    }

    public static ErrorResponse of(int status, String message, Map<String, String> validations) {
        return new ErrorResponse(status, message, validations);
    }
}
