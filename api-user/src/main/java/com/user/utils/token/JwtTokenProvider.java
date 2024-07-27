package com.user.utils.token;

import com.user.enums.ErrorType;
import com.user.enums.TokenType;
import com.user.utils.error.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public JwtTokenProvider(@Value("${jwt.access-token.secret}") String accessSecret,
                            @Value("${jwt.access-token.valid-time}") Duration accessValidTime,
                            @Value("${jwt.refresh-token.secret}") String refreshSecret,
                            @Value("${jwt.refresh-token.valid-time}") Duration refreshValidTime) {
        byte[] secretBytes = Base64.getDecoder().decode(accessSecret);
        TokenType.ACCESS.setSecretKey(Keys.hmacShaKeyFor(secretBytes));
        TokenType.ACCESS.setValidityMilliseconds(accessValidTime.toMillis());
        secretBytes = Base64.getDecoder().decode(refreshSecret);
        TokenType.REFRESH.setSecretKey(Keys.hmacShaKeyFor(secretBytes));
        TokenType.REFRESH.setValidityMilliseconds(refreshValidTime.toMillis());
    }

    public String generateToken(TokenType tokenType, Long userId, Date now) {
        Date expiration = new Date(now.getTime() + tokenType.getValidityMilliseconds());
        return Jwts.builder()
                .subject(tokenType.name())
                .signWith(tokenType.getSecretKey())
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public Long getUserId(TokenType tokenType, String token) {
        Claims claims = getClaims(tokenType, token);
        return claims.get("userId", Long.class);
    }

    private Claims getClaims(TokenType tokenType, String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(tokenType.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String subject = claims.getSubject();
            validateSubject(tokenType, subject);
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CommonException(ErrorType.UNAUTHORIZED_TOKEN);
        }
    }

    private void validateSubject(TokenType tokenType, String subject) {
        if (subject == null || !subject.equals(tokenType.name())) {
            throw new CommonException(ErrorType.UNAUTHORIZED_TOKEN);
        }
    }
}
