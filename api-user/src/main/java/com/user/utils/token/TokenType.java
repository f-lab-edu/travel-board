package com.user.utils.token;

import com.user.utils.token.JwtTokenProperties.TokenProperty;

import java.util.EnumMap;
import java.util.Map;

public enum TokenType {

    ACCESS,
    REFRESH;

    private static final Map<TokenType, TokenProperty> TOKEN_TYPE_TO_TOKEN_PROPERTY =
            new EnumMap<>(TokenType.class);

    public TokenProperty getTokenProperty() {
        return TOKEN_TYPE_TO_TOKEN_PROPERTY.get(this);
    }

    public void setTokenProperty(TokenProperty tokenProperty) {
        TOKEN_TYPE_TO_TOKEN_PROPERTY.put(this, tokenProperty);
    }
}
