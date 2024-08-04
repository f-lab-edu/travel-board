package com.user.dto.response;

public record TokenResponse(String accessToken) {

    public static TokenResponse of(String accessToken) {
        return new TokenResponse(accessToken);
    }
}
