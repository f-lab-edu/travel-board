package com.user.controller.response;

import java.time.Duration;
import java.time.LocalDateTime;

public record UserRegisterResponse(
        Long userId,
        String refreshTokenValue,
        LocalDateTime refreshTokenExpiredAt
) {

    public static UserRegisterResponse of(Long userId, String tokenValue, LocalDateTime expiredAt) {
        return new UserRegisterResponse(userId, tokenValue, expiredAt);
    }

    public long getMaxAgeForCookie() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, refreshTokenExpiredAt);
        return duration.getSeconds();
    }
}
