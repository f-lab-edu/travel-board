package com.user.utils.token;

public record TokenPayload(String email, Long userId, Long accountId) {

    public static TokenPayload of(String email, Long userId, Long accountId) {
        return new TokenPayload(email, userId, accountId);
    }
}
