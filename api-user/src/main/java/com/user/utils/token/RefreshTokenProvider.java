package com.user.utils.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class RefreshTokenProvider {

    private final Key secretKey;
    private final long validityInMillisSeconds;
    private static final String TOKEN_SUBJECT = "REFRESH";

    public RefreshTokenProvider(@Value("${jwt.refresh-token.secret}") String secret,
                                @Value("${jwt.refresh-token.valid-time-in-days}") long validityInDays) {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.validityInMillisSeconds = validityInDays * 24L * 3600L * 1000L;
    }

    public String generateToken(Date now) {
        Date expiration = new Date(now.getTime() + validityInMillisSeconds);
        return Jwts.builder()
                .subject(TOKEN_SUBJECT)
                .signWith(secretKey)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

}
