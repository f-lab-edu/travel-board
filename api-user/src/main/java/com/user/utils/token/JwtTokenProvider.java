package com.user.utils.token;

import com.user.enums.TokenType;
import com.user.utils.error.CommonException;
import com.user.enums.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey accessSecretKey;
    private final Long accessValidityMilliseconds;
    private final SecretKey refreshSecretKey;
    private final Long refreshValidityMilliseconds;

    public JwtTokenProvider(@Value("${jwt.access-token.secret}") String accessSecret,
                            @Value("${jwt.access-token.valid-time}") Duration accessValidTime,
                            @Value("${jwt.refresh-token.secret}") String refreshSecret,
                            @Value("${jwt.refresh-token.valid-time}") Duration refreshValidTime) {
        byte[] secretBytes = Base64.getDecoder().decode(accessSecret);
        this.accessSecretKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessValidityMilliseconds = accessValidTime.toMillis();
        secretBytes = Base64.getDecoder().decode(refreshSecret);
        this.refreshSecretKey = Keys.hmacShaKeyFor(secretBytes);
        this.refreshValidityMilliseconds = refreshValidTime.toMillis();
    }

    public String generateToken(TokenType tokenType, Long userId, Date now) {
        SecretKey secretKey = getSecretKey(tokenType);
        Long validityMilliseconds = tokenType == TokenType.ACCESS ? accessValidityMilliseconds : refreshValidityMilliseconds;
        Date expiration = new Date(now.getTime() + validityMilliseconds);
        return Jwts.builder()
                .subject(tokenType.name())
                .signWith(secretKey)
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
        SecretKey secretKey = getSecretKey(tokenType);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
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

    private SecretKey getSecretKey(TokenType tokenType) {
        return tokenType == TokenType.ACCESS ? accessSecretKey : refreshSecretKey;
    }

    private void validateSubject(TokenType tokenType, String subject) {
        if (subject == null || !subject.equals(tokenType.name())) {
            throw new CommonException(ErrorType.UNAUTHORIZED_TOKEN);
        }
    }
}
