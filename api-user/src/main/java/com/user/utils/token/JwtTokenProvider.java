package com.user.utils.token;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    public String generateToken(TokenType tokenType, Long userId, Date now) {
        Date expiration = new Date(now.getTime() + tokenType.getTokenProperty().getValidityInMillisSeconds());
        return Jwts.builder()
                .subject(tokenType.name())
                .signWith(tokenType.getTokenProperty().getSecretKey())
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

}
