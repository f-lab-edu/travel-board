package com.user.utils.token;

import java.util.EnumMap;
import java.util.Map;

public enum TokenType {
    ACCESS,
    REFRESH;

    public static final Map<TokenType, TokenProperties.TokenProperty> TOKEN_TYPE_TO_TOKEN_PROPERTY =
            new EnumMap<>(TokenType.class);

    public TokenProperties.TokenProperty getTokenProperty() {
        return TOKEN_TYPE_TO_TOKEN_PROPERTY.get(this);
    }
}
