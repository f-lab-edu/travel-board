package com.user.controller.response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisterResponse(
        Long userId,
        UUID refreshTokenValue,
        LocalDateTime refreshTokenExpiredAt
) {

    public static UserRegisterResponse of(Long userId, UUID tokenValue, LocalDateTime expiredAt) {
        return new UserRegisterResponse(userId, tokenValue, expiredAt);
    }

    public Duration getMaxAgeForCookie() {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, refreshTokenExpiredAt);
    }
}
