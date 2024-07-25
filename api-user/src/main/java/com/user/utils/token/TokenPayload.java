package com.user.utils.token;

import io.jsonwebtoken.Claims;

public record TokenPayload(String email, Long userId, Long accountId) {

    public static TokenPayload from(Claims claims) {
        String email = claims.get("email", String.class);
        Long userId = claims.get("userId", Long.class);
        Long accountId = claims.get("accountId", Long.class);
        return new TokenPayload(email, userId, accountId);
    }
}
