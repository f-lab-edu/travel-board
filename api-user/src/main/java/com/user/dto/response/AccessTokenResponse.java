package com.user.dto.response;

public record AccessTokenResponse(String accessToken) {

    public static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
