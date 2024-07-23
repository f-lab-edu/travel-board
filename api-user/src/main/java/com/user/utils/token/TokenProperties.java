package com.user.utils.token;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class TokenProperties {

    private final TokenProperty accessToken;
    private final TokenProperty refreshToken;

    public TokenProperties(TokenProperty accessToken, TokenProperty refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        TokenType.ACCESS.setTokenProperty(accessToken);
        TokenType.REFRESH.setTokenProperty(refreshToken);
    }

    @Getter
    public static class TokenProperty {

        private final Key secretKey;
        private final long validityInMillisSeconds;

        public TokenProperty(String secret, Duration validTime) {
            byte[] secretBytes = Base64.getDecoder().decode(secret);
            this.secretKey = Keys.hmacShaKeyFor(secretBytes);
            this.validityInMillisSeconds = validTime.toMillis();
        }
    }
}